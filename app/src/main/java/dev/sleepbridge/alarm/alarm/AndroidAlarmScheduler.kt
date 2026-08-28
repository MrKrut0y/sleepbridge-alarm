package dev.sleepbridge.alarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import dev.sleepbridge.alarm.receiver.AlarmFireReceiver
import java.time.ZonedDateTime

class AndroidAlarmScheduler(private val context: Context) {
    fun schedule(alarmAt: ZonedDateTime, label: String, alsoCreateClockAlarm: Boolean) {
        scheduleInAppAlarm(alarmAt)
        if (alsoCreateClockAlarm) {
            createSystemClockAlarm(alarmAt, label)
        }
    }

    private fun scheduleInAppAlarm(alarmAt: ZonedDateTime) {
        val intent = Intent(context, AlarmFireReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAtMillis = alarmAt.toInstant().toEpochMilli()
        val info = AlarmManager.AlarmClockInfo(triggerAtMillis, launchIntent())
        alarmManager.setAlarmClock(info, pendingIntent)
    }

    private fun createSystemClockAlarm(alarmAt: ZonedDateTime, label: String) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(AlarmClock.EXTRA_HOUR, alarmAt.hour)
            putExtra(AlarmClock.EXTRA_MINUTES, alarmAt.minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, label)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
        }
        runCatching { context.startActivity(intent) }
    }

    private fun launchIntent(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: Intent()
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val REQUEST_CODE = 7001
    }
}
