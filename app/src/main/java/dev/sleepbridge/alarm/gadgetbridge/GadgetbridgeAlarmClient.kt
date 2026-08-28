package dev.sleepbridge.alarm.gadgetbridge

import android.content.Context
import android.content.Intent
import java.time.ZonedDateTime

class GadgetbridgeAlarmClient(
    private val context: Context,
    private val gadgetbridgePackage: String
) {
    fun replaceOwnedAlarm(deviceAddress: String, alarmAt: ZonedDateTime, title: String) {
        dismissByTitle(deviceAddress, title)
        setAlarm(deviceAddress, alarmAt, title)
    }

    private fun setAlarm(deviceAddress: String, alarmAt: ZonedDateTime, title: String) {
        val intent = Intent(GadgetbridgeContract.ACTION_SET_ALARM).apply {
            setPackage(gadgetbridgePackage)
            putExtra(GadgetbridgeContract.EXTRA_DEVICE, deviceAddress)
            putExtra(GadgetbridgeContract.EXTRA_HOUR, alarmAt.hour)
            putExtra(GadgetbridgeContract.EXTRA_MINUTES, alarmAt.minute)
            putExtra(GadgetbridgeContract.EXTRA_TITLE, title)
        }
        context.sendBroadcast(intent)
    }

    private fun dismissByTitle(deviceAddress: String, title: String) {
        val intent = Intent(GadgetbridgeContract.ACTION_DISMISS_ALARM).apply {
            setPackage(gadgetbridgePackage)
            putExtra(GadgetbridgeContract.EXTRA_DEVICE, deviceAddress)
            putExtra(GadgetbridgeContract.EXTRA_MODE, GadgetbridgeContract.MODE_TITLE)
            putExtra(GadgetbridgeContract.EXTRA_TITLE, title)
        }
        context.sendBroadcast(intent)
    }
}
