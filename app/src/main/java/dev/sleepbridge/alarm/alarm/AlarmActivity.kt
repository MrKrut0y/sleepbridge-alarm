package dev.sleepbridge.alarm.alarm

import android.app.Activity
import android.media.AudioAttributes
import android.net.Uri
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import dev.sleepbridge.alarm.data.SettingsStore

class AlarmActivity : Activity() {
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOn()
        playAlarm()
        vibrate()
        setContentView(content())
    }

    override fun onDestroy() {
        ringtone?.stop()
        vibrator?.cancel()
        super.onDestroy()
    }

    private fun content(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(48, 48, 48, 48)
        addView(TextView(context).apply {
            text = "Wake up"
            textSize = 34f
            gravity = Gravity.CENTER
        })
        addView(Button(context).apply {
            text = "Dismiss"
            setOnClickListener { finish() }
        })
    }

    private fun turnScreenOn() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
    }

    private fun playAlarm() {
        val settingsUri = SettingsStore(this).read().alarmAudioUri
        val uri = settingsUri.toUriOrNull()
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        ringtone = RingtoneManager.getRingtone(this, uri)?.apply {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            }
            play()
        }
    }

    private fun String.toUriOrNull(): Uri? = takeIf { it.isNotBlank() }?.let(Uri::parse)

    private fun vibrate() {
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            getSystemService(VibratorManager::class.java).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 700, 300, 700, 300, 1200)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 1))
    }
}
