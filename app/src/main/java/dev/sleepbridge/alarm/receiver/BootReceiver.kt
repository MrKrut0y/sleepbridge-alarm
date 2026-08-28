package dev.sleepbridge.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sleepbridge.alarm.data.SettingsStore
import dev.sleepbridge.alarm.service.SleepWatchService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED && SettingsStore(context).read().enabled) {
            SleepWatchService.start(context)
        }
    }
}
