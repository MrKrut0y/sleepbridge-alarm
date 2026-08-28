package dev.sleepbridge.alarm.gadgetbridge

object GadgetbridgeContract {
    const val ACTION_FELL_ASLEEP = "nodomain.freeyourgadget.gadgetbridge.FellAsleep"
    const val ACTION_SET_ALARM = "nodomain.freeyourgadget.gadgetbridge.command.SET_ALARM"
    const val ACTION_DISMISS_ALARM = "nodomain.freeyourgadget.gadgetbridge.command.DISMISS_ALARM"

    const val EXTRA_DEVICE = "device"
    const val EXTRA_HOUR = "hour"
    const val EXTRA_MINUTES = "minutes"
    const val EXTRA_TITLE = "title"
    const val EXTRA_MODE = "mode"
    const val MODE_TITLE = "title"
}
