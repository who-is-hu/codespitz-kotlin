package day3

import kotlinx.datetime.Instant

abstract class Scheduler {
    private val senders = hashSetOf<Sender>()

    fun addSender(vararg sender: Sender){
        senders += sender
    }

    fun send(message: Message, now: Instant){
        if(isSend(now)) {
            senders.forEach { it.send(message) }
        }
    }

    protected abstract fun isSend(now: Instant): Boolean
}