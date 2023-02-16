package day6

import day6.launcher.ContinuationTask
import java.util.concurrent.Executors

interface Dispatcher {
//    fun start(looper: day6.launcher.EventLooper)
//    fun start(looper: SerialTask)
    fun start(looper: ContinuationTask)
    fun join()
    class FixedDispatcher(private val numOfThread: Int): Dispatcher {
        private val executor = Executors.newFixedThreadPool(numOfThread)
        override fun start(looper: ContinuationTask) {
            executor.execute(looper)
        }

        override fun join() {
            while(!executor.isShutdown){}
        }
    }
}