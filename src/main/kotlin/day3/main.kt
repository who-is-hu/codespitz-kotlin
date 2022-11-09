package day3

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

fun main() {
    threadLooper.start()
    val user = User("hihi")
    do {
        println("add item")
        val item = Item(read("content:"),read("title:"))
        readN("add Scheduler", "1.once 2.repeatDay", "1", "2") {
            val scheduler: Scheduler = when(it){
                "1" -> getOnce()
                "2" -> getOnce()
                else -> throw Throwable()
            }
            readN("add Sender", "1.print 2.gmail", "1","2") {
                scheduler.addSender(when(it){
                    "1" -> PrintSender()
                    "2" -> PrintSender()
                    else -> throw Throwable()
                })
            }
            item.addSchedules(scheduler)
        }
        user.addItem(item)
    } while (true)
}

fun read(msg: String, vararg answer: String, validator: ((String) -> Boolean)? = null): String{
    do {
        println(msg)
        val line = readLine()
        if (line != null && line.isNotBlank()){
            if(answer.isEmpty() || answer.any { it == line }){
                if(validator == null || validator(line)){
                    return line
                }
            }
        }
    } while (true)
}

fun readN(title: String, msg: String, vararg validator: String, block: (String)->Unit){
    var itemMsg = msg
    var itemValidator = validator
    do {
        println(title)
        val line = read(itemMsg, *itemValidator)
        if (line == "0") break
        block(line)
        itemMsg = "0.no more " + itemMsg
        itemValidator = itemValidator.toMutableList().also { it += "0" }.toTypedArray()
    } while (true)
}

fun getOnce(): Scheduler {
    println("once from now")
    println("unit: ")
    val unit: DateTimeUnit.TimeBased = when(read("1.h 2.m 3.s","1","2","3")){
        "1" -> DateTimeUnit.HOUR
        "2" -> DateTimeUnit.MINUTE
        "3" -> DateTimeUnit.SECOND
        else -> throw Throwable()
    }
    val count = read("count(int): ") { it.toIntOrNull() != null }.toInt()
    return OneTime(Clock.System.now().plus(count, unit))
}