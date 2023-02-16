package day6

import day6.launcher.ContinuationTask

//fun main(){
//    val looper = day6.launcher.EventLooper(Dispatcher.FixedDispatcher(10))
//    for(i in 0..5) looper.linkedTask(
//        {
//            println("$i-0 ${Thread.currentThread().id}")
//            Thread.sleep(100L * i)
//            it.resume()
//        },
//        {
//            println("$i-1 ${Thread.currentThread().id}")
//            Thread.sleep(100L * i)
//            it.resume()
//        },
//        {
//            println("$i-2 ${Thread.currentThread().id}")
//            Thread.sleep(100L * i)
//            it.resume()
//        }
//    )
//    looper.launch()
//    looper.join()
//}

fun main() {
    val dispatcher = Dispatcher.FixedDispatcher(10)
    for (i in 0..5) {
        val looper = SerialTask(dispatcher,
            {
                println("$i-0 ${Thread.currentThread().id}")
                Thread.sleep(100L * i)
                it.resume()
            },
            {
                println("$i-1 ${Thread.currentThread().id}")
                Thread.sleep(100L * i)
                it.resume()
            },
            {
                println("$i-2 ${Thread.currentThread().id}")
                Thread.sleep(100L * i)
                it.resume()
            })
        looper.launch()
    }
    dispatcher.join()
}

fun main() {
    val dispatcher = Dispatcher.FixedDispatcher(10)
    for (i in 0..5) {
        val looper = ContinuationTask(false,dispatcher) {
            when(it.step){
                0 -> {
                    println("$i-0 ${Thread.currentThread().id}")
                    it.resume(1)
                }
                1 -> {
                    println("$i-1 ${Thread.currentThread().id}")
                    it.resume(2)
                }
                2 -> {
                    println("$i-2 ${Thread.currentThread().id}")
                    it.complete()
                }
            }
        }
        looper.launch()
    }
    dispatcher.join()
}
