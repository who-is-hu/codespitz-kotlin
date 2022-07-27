import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

@Target(AnnotationTarget.PROPERTY)
annotation class Ignore

@Target(AnnotationTarget.PROPERTY)
annotation class Name(val name: String)

class Json0(@Ignore val a: Int, @Name("title") val b: String)

fun main(args: Array<String>) {
    val stringBuilder = StringBuilder()
    val jsonString = stringBuilder.stringify(Json0(3, "abc"))
    println(jsonString)
}

fun StringBuilder.stringify(value: Any): String {
    jsonValue(value)
    return toString()
}

fun StringBuilder.comma() {
    append(",")
}

// 수신 객체 지정 람다는 Function1으로 바뀜
// Function1::invoke(StringBuilder)
fun StringBuilder.wrap(begin: Char, end: Char, block: StringBuilder.() -> Unit) {
    append(begin)
    block() // == this.block() == block(this)
    append(end)
}

// Function0 으로 바뀜
// Function0::invoke()
fun StringBuilder.wrap2(begin: Char, end: Char, block: () -> Unit) {
    append(begin)
    block()
    // this.block() invoke가 받는게 없어서 this 사용불가
    append(end)
}

// 람다 본문 this 사용 불가
//fun test(){
//    val stringBuilder = StringBuilder()
//    stringBuilder.wrap2('a','a') {
//        println(this)
//    }
//}

// 람다 밖에서 this 를 찾을수 있을때 사용가능
class Test(val prop: String) {
    fun testMethod(): Unit {
        val stringBuilder = StringBuilder()
        stringBuilder.wrap2('a','a') {
            println(this.prop)
        }
    }
}

// 전역 레벨 private 은 file 안에서
private fun StringBuilder.jsonValue(value: Any?) {
    when (value) {
        null -> append("null") // 여길 타면 아래는 null 이 아니게 smart casting
        is String -> jsonString(value)
        is Number, is Boolean -> append("$value")
        is List<*> -> jsonList(value)
        else -> jsonObject(value)
    }
}

private fun StringBuilder.jsonString(v: String) {
    append(""""${v.replace("\"", "\\\"")}"""")
}

private fun StringBuilder.jsonList(target: List<*>) {
    wrap('[', ']') {
        target.joinTo(::comma) { jsonValue(it) }
    }
}


private fun <T : Any> StringBuilder.jsonObject(target: T) {
    wrap('{', '}') {
        target::class.members.filterIsInstance<KProperty<*>>()
            .filter { !it.hasAnnotation<Ignore>() }
            .joinTo(::comma) { // this에서 찾고 없으면 전역에서 찾음
                //여기서 KProperty로 변환되서 옴
                //property는 getter를 가지고있고 var이면 setter도
                jsonValue(it.findAnnotation<Name>()?.name ?: it.name)
                append(":")
                jsonValue(it.getter.call(target))
            }
    }
}

fun <T> Iterable<T>.joinTo(sep: () -> Unit, transform: (T) -> Unit) {
    forEachIndexed { count, element ->
        if (count != 0) sep();
        transform(element);
    }
}