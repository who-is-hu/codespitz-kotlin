fun main(args: Array<String>){
    println(calc("-2 * -3 + 0.4 / -0.2"))
}

val trim = """[^.\d-+*/()]""".toRegex()
val groupMD = """((?:\+|\+-)?[.\d]+)([*/])((?:\+|\+-)?[.\d]+)""".toRegex()

fun trim(v: String): String {
    return v.replace(trim, "")
}
fun repMtoPM(v: String) = v.replace("-", "+-")
fun foldGroup(v: String): Double = groupMD.findAll(v).fold(0.0){ acc, curr ->
    val (_, left, op, right) = curr.groupValues
    val leftValue = left.replace("+", "").toDouble()
    val rightValue = right.replace("+", "").toDouble()
    val result = when(op) {
        "*" -> leftValue * rightValue
        "/" -> leftValue / rightValue
        else -> throw Throwable("invalid op $op")
    }
    return acc + result
}

fun calc(v: String) = foldGroup((repMtoPM(trim(v))))

