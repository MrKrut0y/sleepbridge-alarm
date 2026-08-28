package dev.sleepbridge.alarm.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime

class SleepAlarmCalculatorTest {
    @Test
    fun addsConfiguredSleepDuration() {
        val start = ZonedDateTime.of(2026, 8, 29, 23, 45, 0, 0, ZoneId.of("Europe/Samara"))
        val planned = SleepAlarmCalculator().plan(start, Duration.ofHours(7).plusMinutes(30))

        assertEquals(2026, planned.alarmAt.year)
        assertEquals(8, planned.alarmAt.monthValue)
        assertEquals(30, planned.alarmAt.dayOfMonth)
        assertEquals(7, planned.alarmAt.hour)
        assertEquals(15, planned.alarmAt.minute)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsZeroDuration() {
        SleepAlarmCalculator().plan(ZonedDateTime.now(), Duration.ZERO)
    }
}
