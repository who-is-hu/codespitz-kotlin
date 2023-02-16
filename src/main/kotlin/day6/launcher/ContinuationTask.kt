package day6.launcher

import day6.Dispatcher
import day6.Stat
import day6.Task
import day6.dsl.Continuation

class ContinuationTask(
    isLazy: Boolean,
    private val dispatcher: Dispatcher,
    block: (Continuation) -> Unit,
) : Runnable {
    private val task: Task = Task(block)

    init {
        if (!isLazy) {
            launch()
        }
    }

    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            Thread.sleep(5)
            if(task.isCompleted == Stat.MARK) break
            if (task.isStarted == Stat.MARK){
                task.isStarted = Stat.CONFIRM
                task.run(task.continuation)
            }
        }
        task.continuation.failed?.let { throw it }
    }

    fun launch(){
        dispatcher.start(this)
    }
}