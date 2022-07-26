import kotlin.reflect.KProperty

class Json0(val a: Int, val b: String)

fun main(args: Array<String>) {
    val stringBuilder = StringBuilder()
    val jsonString = stringBuilder.stringify(Json0(3, "abc"))
    println(jsonString)
}

fun StringBuilder.stringify(value: Any): String {
    jsonValue(value)
    return toString()
}

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
    append( """"${v.replace("\"", "\\\"")}"""")
}

private fun StringBuilder.jsonList(target: List<*>) {
    append("[")
    target.joinTo({append(",")}) { jsonValue(it) }
    append("]")
}

private fun <T : Any> StringBuilder.jsonObject(target: T) {
    append("{")
    target::class.members.filterIsInstance<KProperty<*>>()
        .joinTo({ append(",") }) {
            //여기서 KProperty로 변환되서 옴
            //property는 getter를 가지고있고 var이면 setter도
            jsonValue(it.name)
            append(":")
            jsonValue(it.getter.call(target))
        }
    append("}")
}

fun <T> Iterable<T>.joinTo(sep: () -> Unit, transform: (T) -> Unit) {
    forEachIndexed { count, element ->
        if (count != 0) sep();
        transform(element);
    }
}