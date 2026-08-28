package dev.sleepbridge.alarm.data

import android.content.Context

class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("sleepbridge", Context.MODE_PRIVATE)

    fun read(): UserSettings = UserSettings(
        enabled = prefs.getBoolean(KEY_ENABLED, true),
        sleepHours = prefs.getInt(KEY_SLEEP_HOURS, 8),
        sleepMinutes = prefs.getInt(KEY_SLEEP_MINUTES, 0),
        gadgetbridgePackage = prefs.getString(KEY_GADGETBRIDGE_PACKAGE, DEFAULT_GADGETBRIDGE_PACKAGE)
            ?: DEFAULT_GADGETBRIDGE_PACKAGE,
        bandMacAddress = prefs.getString(KEY_BAND_MAC, "") ?: "",
        bandAlarmTitle = prefs.getString(KEY_BAND_ALARM_TITLE, DEFAULT_ALARM_TITLE) ?: DEFAULT_ALARM_TITLE,
        setAndroidClockAlarm = prefs.getBoolean(KEY_SET_ANDROID_CLOCK_ALARM, true),
        setBandAlarm = prefs.getBoolean(KEY_SET_BAND_ALARM, true)
    )

    fun save(settings: UserSettings) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, settings.enabled)
            .putInt(KEY_SLEEP_HOURS, settings.sleepHours.coerceIn(0, 24))
            .putInt(KEY_SLEEP_MINUTES, settings.sleepMinutes.coerceIn(0, 59))
            .putString(KEY_GADGETBRIDGE_PACKAGE, settings.gadgetbridgePackage.trim())
            .putString(KEY_BAND_MAC, settings.bandMacAddress.trim().uppercase())
            .putString(KEY_BAND_ALARM_TITLE, settings.bandAlarmTitle.trim())
            .putBoolean(KEY_SET_ANDROID_CLOCK_ALARM, settings.setAndroidClockAlarm)
            .putBoolean(KEY_SET_BAND_ALARM, settings.setBandAlarm)
            .apply()
    }

    companion object {
        const val DEFAULT_GADGETBRIDGE_PACKAGE = "nodomain.freeyourgadget.gadgetbridge"
        const val DEFAULT_ALARM_TITLE = "Sleepbridge Alarm"

        private const val KEY_ENABLED = "enabled"
        private const val KEY_SLEEP_HOURS = "sleep_hours"
        private const val KEY_SLEEP_MINUTES = "sleep_minutes"
        private const val KEY_GADGETBRIDGE_PACKAGE = "gadgetbridge_package"
        private const val KEY_BAND_MAC = "band_mac"
        private const val KEY_BAND_ALARM_TITLE = "band_alarm_title"
        private const val KEY_SET_ANDROID_CLOCK_ALARM = "set_android_clock_alarm"
        private const val KEY_SET_BAND_ALARM = "set_band_alarm"
    }
}
