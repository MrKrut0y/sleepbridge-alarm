package dev.sleepbridge.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sleepbridge.alarm.alarm.AlarmActivity

class AlarmFireReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(activityIntent)
    }
}
