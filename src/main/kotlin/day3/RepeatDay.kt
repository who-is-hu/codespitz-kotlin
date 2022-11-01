package day3

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek

class RepeatDay(
    private val hour: Int,
    private val minute: Int,
    private vararg val days: DayOfWeek,
):Scheduler() {
    private val isSent = hashMapOf<String, Boolean>()
    override fun isSend(now: Instant): Boolean {
        val dateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val nowDay = dateTime.dayOfWeek
        val nowHour = dateTime.hour
        val nowMinute = dateTime.minute
        val key = "$nowDay$nowHour$nowMinute"

        if(isSent[key] == true) return false
        if(nowDay !in days) return false
        if(nowHour < hour) return false
        if(nowHour == hour && nowMinute < minute) return false
        isSent[key] = true
        return true
    }
}