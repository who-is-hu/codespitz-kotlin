package day3

import kotlinx.datetime.Instant

class OneTime(private val at: Instant): Scheduler() {
    private var isSent = false
    override fun isSend(now: Instant): Boolean {
        return if(isSent || now <= at) false else {
            isSent = true
            true
        }
    }
}