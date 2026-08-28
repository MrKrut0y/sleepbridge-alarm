package dev.sleepbridge.alarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.sleepbridge.alarm.receiver.AlarmFireReceiver
import java.time.ZonedDateTime

class AndroidAlarmScheduler(private val context: Context) {
    fun schedule(alarmAt: ZonedDateTime) {
        scheduleInAppAlarm(alarmAt)
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
