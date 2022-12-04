package day4

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation

fun <T: Any> parseJson(target: T, json: String): T? {
    val lexer = JsonLexer(json)
    lexer.skipWhite()
    return parseObject(lexer, target)
}

fun <T:Any> T.fromJson(json:String): T? {
    return parseJson(this, json)
}

class JsonLexer(val json: String){
    val last = json.lastIndex
    var cursor = 0
        private set
    inline val curr
        get() = json[cursor]
    // inline 안은 inline 보다 가시성이 높건 같아야함
    // 너무 inline 만 하려하면 캡슐화가 깨지게될 수 있음
    fun next(){ if(cursor < last) ++cursor }
    inline fun skipWhite(){
        while (" \r\t\n".indexOf(curr) != -1 && cursor < last) next()
    }
    inline fun isObjectOpen(): Boolean = curr == '{'
    inline fun isObjectClose(): Boolean = curr == '}'
    inline fun isComma(): Boolean = curr == ','
    inline fun isOpenArray(): Boolean = curr == '['
    inline fun isCloseArray(): Boolean = curr == ']'

    inline fun key(): String? {
        val result = string() ?: return null
        skipWhite()
        if(curr != ':') return null
        next()
        skipWhite()
        return result
    }

    inline fun string(): String? {
        if(curr != '"') return null
        next()
        val start = cursor
        var isSkip = false
        while (isSkip || curr != '"' ){
            isSkip = if(isSkip) false else curr == '\\'
            next()
        }
        val result = json.substring(start, cursor)
        next()
        return result
    }

    inline fun number(): String? {
        val start = cursor
        while ("-.0123456789".indexOf(curr) != -1) next()
        return if(start == cursor) null else json.substring(start,cursor)
    }
    inline fun int(): Int? = number()?.toInt()
    inline fun long(): Long? = number()?.toLong()
    inline fun double(): Double? = number()?.toDouble()
    inline fun float(): Float? = number()?.toFloat()
    fun boolean(): Boolean? = when {
        json.substring(cursor, cursor+4) == "true" -> {
            cursor += 4
            true
        }
        json.substring(cursor, cursor + 5) == "false" -> {
            cursor += 5
            false
        }
        else -> null
    }
}

@Target(AnnotationTarget.PROPERTY)
annotation class Name(val name: String)

// 컴파일하면 순환참조를 찾아내서 inline 에러가 남
fun <T:Any> parseObject(lexer: JsonLexer, target: T): T? {
    if(!lexer.isObjectOpen()) return null
    lexer.next()
    val props = target::class.members
        .filterIsInstance<KMutableProperty<*>>()
        .associateBy { (it.findAnnotation<Name>()?.name ?: it.name) }
    while (!lexer.isObjectClose()){
        lexer.skipWhite()
        val key = lexer.key() ?: return null
        val prop = props[key] ?: return null
        val value = jsonValue(lexer, prop.returnType)
        prop.setter.call(target, value)
        lexer.skipWhite()
        if(lexer.isComma()) lexer.next()
    }
    lexer.next()
    return target
}

inline fun jsonValue(lexer: JsonLexer, type: KType): Any? {
    return when (val cls = type.classifier as? KClass<*> ?: return null){
        String::class -> lexer.string()
        Long::class -> lexer.long()
        Int::class -> lexer.int()
        Float::class -> lexer.float()
        Double::class -> lexer.double()
        Boolean::class -> lexer.boolean()
        List::class -> parseList(lexer, type.arguments[0].type?.classifier as? KClass<*> ?: return null)
        else -> parseObject(lexer, cls.createInstance())
    }
}
// 컴파일하면 순환참조를 찾아내서 inline 에러가 남
fun parseList(lexer: JsonLexer, cls: KClass<*>): List<*>?{
    if(!lexer.isOpenArray()) return null
    lexer.next()
    val result = mutableListOf<Any>()
    val type = cls.createType()
    while(!lexer.isCloseArray()){
        lexer.skipWhite()
        val v = jsonValue(lexer, type) ?: return null
        result += v
        if(lexer.isComma()) lexer.next()
    }
    return result
}