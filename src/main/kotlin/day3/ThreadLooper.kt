package day3

import kotlinx.datetime.Clock

//object ThreadLooper: Looper() {
//    private val thread by lazy {
//        Thread{ // java sam일땐 lambda만 넘기면 됌
//            while(isRunning && !Thread.currentThread().isInterrupted){
//                val now = Clock.System.now()
//                Looper.users.forEach { it.send(now) }
//                Thread.sleep(1000)
//            }
//        }
//    }
//
//
//    override fun started() {
//        if(!thread.isAlive) thread.start()
//    }
//
//    override fun ended() {
//    }
//}

val threadLooper = Looper({
    val thread = Thread {
        while (it.isRunning && !Thread.currentThread().isInterrupted) {
            val now = Clock.System.now()
            Looper.users.forEach { it.send(now) }
            Thread.sleep(1000)
        }
    }
    if (!thread.isAlive) thread.start()
}, {})