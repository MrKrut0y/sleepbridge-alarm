package dev.sleepbridge.alarm.data

import java.time.Duration

data class UserSettings(
    val enabled: Boolean,
    val sleepHours: Int,
    val sleepMinutes: Int,
    val gadgetbridgePackage: String,
    val bandMacAddress: String,
    val bandAlarmTitle: String,
    val setAndroidClockAlarm: Boolean,
    val setBandAlarm: Boolean
) {
    val sleepDuration: Duration
        get() = Duration.ofHours(sleepHours.toLong()).plusMinutes(sleepMinutes.toLong())

    val hasBandTarget: Boolean
        get() = bandMacAddress.matches(Regex("^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$"))
}
