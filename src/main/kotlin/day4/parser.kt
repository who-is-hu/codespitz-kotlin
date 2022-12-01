package day4

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

fun <T:Any?> parseJson(target: T, json: String): T? {
    val lexer = JsonLexer(json)
    lexer.skipWhite()
    return parseObject(lexer, target)
}

fun <T:Any> T.fromJson(json:String): T? {
    parseJson(this, json)
}

class JsonLexer(private val json: String){
    private val last = json.lastIndex
    var cursor = 0
        private set
    val curr
        get() = json[cursor]
    fun next(){
        if(cursor < last) ++cursor
    }
    fun skipWhite(){
        while (" \r\t\n".indexOf(curr) != -1 && cursor < last) next()
    }
    fun isObjectOpen(): Boolean = curr == '{'
    fun isObjectClose(): Boolean = curr == '}'
    fun isComma(): Boolean = curr == ','

    fun key(): String? {
        val result = string() ?: return null
        skipWhite()
        if(curr != ':') return null
        next()
        skipWhite()
        return result
    }

    fun string(): String? {
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

    private fun number(): String? {
        val start = cursor
        while ("-.0123456789".indexOf(curr) != -1) next()
        return if(start == cursor) null else json.substring(start,cursor)
    }
    fun int(): Int? = number()?.toInt()
    fun long(): Long? = number()?.toLong()
    fun double(): Double? = number()?.toDouble()
    fun float(): Float? = number()?.toFloat()
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

fun jsonValue(lexer: JsonLexer, type: KType): Any? {
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