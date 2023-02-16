package day6.dsl

import day6.Stat
import day6.Task

class Continuation(private val task: Task){
    var step = 0
        private set
    operator fun get(key: String) = task.env[key]
    operator fun set(key:String, value: Any?) {
        task.env[key] = value
    }
    internal var failed: Throwable? = null
    fun cancel(throwable: Throwable){
        failed = Throwable("step:${step}, context:${task.env}", throwable)
        task.isCompleted = Stat.MARK
    }
    fun complete(){
        task.isCompleted = Stat.MARK
    }
    fun resume(step: Int){
        this.step = step
        task.isStarted = Stat.READY
    }
}