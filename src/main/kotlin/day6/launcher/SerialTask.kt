package day6.launcher

import day6.Dispatcher
import day6.Stat
import day6.Task
import day6.dsl.Controller

class SerialTask(private val dispatcher: Dispatcher, vararg blocks: (Controller) -> Unit) : Runnable {
    private var task: Task

    init {
        if (blocks.isEmpty()) throw Throwable("err")
        var prev = Task(blocks[0])
        task = prev
        for (i in 1..blocks.lastIndex) {
            val task = Task(blocks[i])
            prev.next = task
            prev = task
        }
    }

    override fun run() {
        while (!Thread.currentThread().isInterrupted) {
            Thread.sleep(16)
            if (task.isCompleted == Stat.MARK) { // var 은 스레드 환경에서 언제든 바뀔수있어서 null 체크해도 스마트캐스팅을 허용하지 않음
                task.next?.let {
                    it.isStarted = Stat.MARK
                    task = it
                }
            }
            if (task.isStarted == Stat.MARK){
                task.run(Controller(task))
                task.isStarted = Stat.CONFIRM
            }
        }
    }

    fun launch(){
        dispatcher.start(this)
    }
}