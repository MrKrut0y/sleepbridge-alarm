package dev.sleepbridge.alarm.core

import android.content.Context
import android.widget.Toast
import dev.sleepbridge.alarm.alarm.AndroidAlarmScheduler
import dev.sleepbridge.alarm.data.SettingsStore
import dev.sleepbridge.alarm.gadgetbridge.GadgetbridgeAlarmClient
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SleepAlarmEngine(private val context: Context) {
    private val store = SettingsStore(context)
    private val prefs = context.applicationContext.getSharedPreferences("sleepbridge_runtime", Context.MODE_PRIVATE)
    private val calculator = SleepAlarmCalculator()
    private val androidAlarmScheduler = AndroidAlarmScheduler(context)

    fun onSleepDetected(now: ZonedDateTime = ZonedDateTime.now()) {
        if (isDuplicate(now)) {
            return
        }

        val settings = store.read()
        if (!settings.enabled) {
            return
        }
        rememberHandled(now)

        val planned = calculator.plan(now, settings.sleepDuration)
        val title = settings.bandAlarmTitle.ifBlank { SettingsStore.DEFAULT_ALARM_TITLE }

        androidAlarmScheduler.schedule(
            alarmAt = planned.alarmAt,
            label = title,
            alsoCreateClockAlarm = settings.setAndroidClockAlarm
        )

        if (settings.setBandAlarm && settings.hasBandTarget) {
            GadgetbridgeAlarmClient(context, settings.gadgetbridgePackage)
                .replaceOwnedAlarm(settings.bandMacAddress, planned.alarmAt, title)
        }

        val time = planned.alarmAt.format(DateTimeFormatter.ofPattern("HH:mm"))
        Toast.makeText(context, "Sleep detected. Alarm set for $time.", Toast.LENGTH_LONG).show()
    }

    private fun isDuplicate(now: ZonedDateTime): Boolean {
        val last = prefs.getLong(KEY_LAST_HANDLED_MILLIS, 0L)
        return last > 0L && now.toInstant().toEpochMilli() - last < DUPLICATE_WINDOW_MILLIS
    }

    private fun rememberHandled(now: ZonedDateTime) {
        prefs.edit().putLong(KEY_LAST_HANDLED_MILLIS, now.toInstant().toEpochMilli()).apply()
    }

    companion object {
        private const val KEY_LAST_HANDLED_MILLIS = "last_handled_millis"
        private const val DUPLICATE_WINDOW_MILLIS = 60_000L
    }
}
