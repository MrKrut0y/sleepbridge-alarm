package dev.sleepbridge.alarm.core

import java.time.Duration
import java.time.ZonedDateTime

data class PlannedAlarm(
    val sleepStartedAt: ZonedDateTime,
    val alarmAt: ZonedDateTime,
    val sleepDuration: Duration
)

class SleepAlarmCalculator {
    fun plan(sleepStartedAt: ZonedDateTime, sleepDuration: Duration): PlannedAlarm {
        require(!sleepDuration.isNegative && !sleepDuration.isZero) {
            "Sleep duration must be positive"
        }
        return PlannedAlarm(
            sleepStartedAt = sleepStartedAt,
            alarmAt = sleepStartedAt.plus(sleepDuration),
            sleepDuration = sleepDuration
        )
    }
}
