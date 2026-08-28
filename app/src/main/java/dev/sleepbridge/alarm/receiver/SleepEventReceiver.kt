package dev.sleepbridge.alarm.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sleepbridge.alarm.core.SleepAlarmEngine
import dev.sleepbridge.alarm.gadgetbridge.GadgetbridgeContract
import dev.sleepbridge.alarm.service.SleepWatchService

class SleepEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == GadgetbridgeContract.ACTION_FELL_ASLEEP || intent.action == SleepWatchService.ACTION_TEST_FELL_ASLEEP) {
            SleepAlarmEngine(context.applicationContext).onSleepDetected()
        }
    }
}
