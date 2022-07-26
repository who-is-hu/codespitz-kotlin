import kotlin.reflect.KProperty

class Json0(val a: Int, val b: String)

fun main(args: Array<String>) {
    println(stringify(Json0(3, "abc")))
}

fun stringify(value: Any): String {
    val builder = StringBuilder()
    jsonValue(value, builder)
    return builder.toString()
}

private fun jsonValue(value: Any?, builder: StringBuilder) {
    when (value) {
        null -> builder.append("null") // 여길 타면 아래는 null 이 아니게 smart casting
        is String -> jsonString(value, builder)
        is Number, is Boolean -> builder.append("$value")
        is List<*> -> jsonList(value, builder)
        else -> jsonObject(value, builder)
    }
}

private fun jsonString(v: String, builder: StringBuilder) {
    builder.append( """"${v.replace("\"", "\\\"")}"""")
}

private fun jsonList(target: List<*>, builder: StringBuilder) {
    builder.append("[")
    target.joinTo({builder.append(",")}) { jsonValue(it,builder)}
    builder.append("]")
}

private fun <T : Any> jsonObject(target: T, builder: StringBuilder): String {
    builder.append("{")
    target::class.members.filterIsInstance<KProperty<*>>()
        .joinTo({ builder.append(",") }) {
            //여기서 KProperty로 변환되서 옴
            //property는 getter를 가지고있고 var이면 setter도
            builder.append("${jsonValue(it.name, builder)}")
            builder.append(":")
            builder.append("${jsonValue(it.getter.call(target), builder)}")
        }.toString()
    builder.append("}")
    return builder.toString()
}

fun <T> Iterable<T>.joinTo(sep: () -> Unit, transform: (T) -> Unit) {
    forEachIndexed { count, element ->
        if (count != 0) sep();
        transform(element);
    }
}