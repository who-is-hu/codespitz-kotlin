package day4

import kotlin.reflect.KProperty

data class Infinity<T>(
    private val value: T,
    private val limit: Int = -1,
    private val nextValue: (T) -> T
) {
    class Iter<T>(private var item: Infinity<T>) : Iterator<T> {
        override fun hasNext(): Boolean = item.limit != 0
        override fun next(): T {
            val result by item
            item = item.next()
            return result
        }
    }

    operator fun iterator() = Iter(this)
    fun next() = Infinity(nextValue(value), limit - 1, nextValue)
    operator fun getValue(ref: Any?, prop: KProperty<*>) = value
}

fun main() {
    // limit 에 대한 요구는 서비스 코드로 가야함
    // host 코드에서 반복되는 패턴이 있다면 service 코드로 옮겨라
//    val a = Infinity(0) { it + 1 }
//    var limit = 20
//    for(i in a){ //java는 iterable 구현체 kotlin은 iterator operator 가지면됨
//        if(limit-- > 0) println("$i") else break
//    }

    val a = Infinity(0, 20) { it + 1 }
    for (i in a) {
        println("$i")
    }
}