package day6.launcher

import day6.dsl.Controller
import day6.Dispatcher
import day6.Task
import java.util.*

class EventLooper(private val dispatcher: Dispatcher): Runnable {
    val tasks: Queue<Task> = LinkedList()
    var currTask: Task? = null
    fun linkedTask(vararg blocks: (Controller) -> Unit){
        if(blocks.isEmpty()) return
        synchronized(tasks){
            var prev = Task(blocks[0])
            tasks.add(prev)
            for (i in 1..blocks.lastIndex){
                val task = Task(blocks[i])
                prev.next = task
                prev = task
            }
        }
    }

    override fun run() {
        while(!Thread.currentThread().isInterrupted){
            Thread.sleep(16)
            synchronized(this){
                if(currTask != null){ // var 은 스레드 환경에서 언제든 바뀔수있어서 null 체크해도 스마트캐스팅을 허용하지 않음
                    currTask?.let { curr -> // let 을 쓰면 currTask 을 불변객체로 받아와서 사용가능
                        if(curr.isCompleted){
                            curr.next?.let { tasks.add(it) }
                            currTask = null
                        }
                    }
                } else tasks.poll()?.let {
                    currTask = it
                    it.run(Controller(it))
                }
            }
        }
    }

    fun launch(){
        dispatcher.start(this)
    }

    fun join(){
        dispatcher.join()
    }
}