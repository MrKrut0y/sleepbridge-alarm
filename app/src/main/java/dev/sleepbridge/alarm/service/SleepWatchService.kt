package dev.sleepbridge.alarm.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import dev.sleepbridge.alarm.core.SleepAlarmEngine
import dev.sleepbridge.alarm.gadgetbridge.GadgetbridgeContract

class SleepWatchService : Service() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == GadgetbridgeContract.ACTION_FELL_ASLEEP || intent.action == ACTION_TEST_FELL_ASLEEP) {
                SleepAlarmEngine(context.applicationContext).onSleepDetected()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(NOTIFICATION_ID, notification())

        val filter = IntentFilter().apply {
            addAction(GadgetbridgeContract.ACTION_FELL_ASLEEP)
            addAction(ACTION_TEST_FELL_ASLEEP)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, RECEIVER_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, filter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        runCatching { unregisterReceiver(receiver) }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun notification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return builder
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Sleepbridge Alarm")
            .setContentText("Listening for Gadgetbridge sleep events")
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sleep event listener",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val ACTION_TEST_FELL_ASLEEP = "dev.sleepbridge.alarm.TEST_FELL_ASLEEP"
        private const val CHANNEL_ID = "sleepbridge_listener"
        private const val NOTIFICATION_ID = 42

        fun start(context: Context) {
            val intent = Intent(context, SleepWatchService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, SleepWatchService::class.java))
        }
    }
}
