package day6

import day6.dsl.Continuation
import day6.dsl.Controller

class Task internal constructor(var run: (Continuation) -> Unit) {
    internal val continuation = Continuation(this)
    internal var isCompleted = Stat.READY
    internal var isStarted = Stat.READY
    internal var result: Result<Any?>? = null
//    internal var next: Task? = null
    internal var env: MutableMap<String, Any?> = hashMapOf()
}



