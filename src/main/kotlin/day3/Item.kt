package day3

import kotlinx.datetime.Instant

class Item(var title: String, var content: String) {
    private val schedules = hashSetOf<Scheduler>()
    fun addSchedules(vararg schedule: Scheduler) {
        schedules += schedule
    }

    fun send(now: Instant) {
        schedules.forEach { it.send(toMessage(), now) }
    }

    fun toMessage() = Message(title, content)
}