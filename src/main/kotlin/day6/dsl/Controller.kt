package day6.dsl

import day6.Stat
import day6.Task

class Controller internal constructor(private val task: Task){
    val data get() = task.result
    fun cancel(throwable: Throwable){
        task.next?.result = Result.failure(throwable)
        task.isCompleted = Stat.MARK
    }
    fun resume(data: Any? = null){
        task.next?.result = Result.success(data)
        task.isCompleted = Stat.MARK
    }
}

