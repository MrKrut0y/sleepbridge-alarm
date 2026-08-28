package dev.sleepbridge.alarm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import dev.sleepbridge.alarm.data.SettingsStore
import dev.sleepbridge.alarm.data.UserSettings
import dev.sleepbridge.alarm.service.SleepWatchService

class MainActivity : Activity() {
    private lateinit var store: SettingsStore

    private lateinit var enabled: CheckBox
    private lateinit var sleepHours: EditText
    private lateinit var sleepMinutes: EditText
    private lateinit var gadgetbridgePackage: EditText
    private lateinit var bandMacAddress: EditText
    private lateinit var alarmTitle: EditText
    private lateinit var setAndroidAlarm: CheckBox
    private lateinit var setBandAlarm: CheckBox
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = SettingsStore(this)
        requestNotificationPermission()
        setContentView(screen())
        loadSettings()
        SleepWatchService.start(this)
    }

    private fun screen(): ScrollView {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 44, 40, 40)
        }

        root.addView(TextView(this).apply {
            text = "Sleepbridge Alarm"
            textSize = 30f
            setTextColor(Color.rgb(24, 24, 21))
        })
        root.addView(TextView(this).apply {
            text = "Gadgetbridge sleep event -> Android alarm -> optional band alarm"
            textSize = 15f
            setTextColor(Color.rgb(85, 85, 80))
            setPadding(0, 8, 0, 28)
        })

        enabled = CheckBox(this).apply { text = "Listen for sleep events" }
        setAndroidAlarm = CheckBox(this).apply { text = "Create Android Clock alarm" }
        setBandAlarm = CheckBox(this).apply { text = "Create Gadgetbridge band alarm" }
        root.addView(enabled)
        root.addView(setAndroidAlarm)
        root.addView(setBandAlarm)

        sleepHours = input("Sleep hours", InputType.TYPE_CLASS_NUMBER)
        sleepMinutes = input("Extra minutes", InputType.TYPE_CLASS_NUMBER)
        gadgetbridgePackage = input("Gadgetbridge package", InputType.TYPE_CLASS_TEXT)
        bandMacAddress = input("Band MAC address, for example AA:BB:CC:DD:EE:FF", InputType.TYPE_CLASS_TEXT)
        alarmTitle = input("Alarm title", InputType.TYPE_CLASS_TEXT)

        root.addView(sleepHours)
        root.addView(sleepMinutes)
        root.addView(gadgetbridgePackage)
        root.addView(bandMacAddress)
        root.addView(alarmTitle)

        root.addView(Button(this).apply {
            text = "Save and start"
            setOnClickListener {
                saveSettings()
                SleepWatchService.start(this@MainActivity)
                Toast.makeText(this@MainActivity, "Saved. Listener is running.", Toast.LENGTH_SHORT).show()
            }
        })
        root.addView(Button(this).apply {
            text = "Test sleep event"
            setOnClickListener {
                saveSettings()
                sendBroadcast(Intent(SleepWatchService.ACTION_TEST_FELL_ASLEEP).setPackage(packageName))
            }
        })
        root.addView(Button(this).apply {
            text = "Stop listener"
            setOnClickListener {
                SleepWatchService.stop(this@MainActivity)
                Toast.makeText(this@MainActivity, "Listener stopped.", Toast.LENGTH_SHORT).show()
            }
        })

        status = TextView(this).apply {
            setPadding(0, 26, 0, 0)
            textSize = 14f
            setTextColor(Color.rgb(70, 70, 66))
            gravity = Gravity.START
        }
        root.addView(status)

        return ScrollView(this).apply { addView(root) }
    }

    private fun input(hintText: String, inputTypeValue: Int): EditText =
        EditText(this).apply {
            hint = hintText
            inputType = inputTypeValue
            singleLine = true
            setPadding(0, 18, 0, 18)
        }

    private fun loadSettings() {
        val settings = store.read()
        enabled.isChecked = settings.enabled
        setAndroidAlarm.isChecked = settings.setAndroidClockAlarm
        setBandAlarm.isChecked = settings.setBandAlarm
        sleepHours.setText(settings.sleepHours.toString())
        sleepMinutes.setText(settings.sleepMinutes.toString())
        gadgetbridgePackage.setText(settings.gadgetbridgePackage)
        bandMacAddress.setText(settings.bandMacAddress)
        alarmTitle.setText(settings.bandAlarmTitle)
        updateStatus(settings)
    }

    private fun saveSettings() {
        val settings = UserSettings(
            enabled = enabled.isChecked,
            sleepHours = sleepHours.text.toString().toIntOrNull()?.coerceIn(0, 24) ?: 8,
            sleepMinutes = sleepMinutes.text.toString().toIntOrNull()?.coerceIn(0, 59) ?: 0,
            gadgetbridgePackage = gadgetbridgePackage.text.toString()
                .ifBlank { SettingsStore.DEFAULT_GADGETBRIDGE_PACKAGE },
            bandMacAddress = bandMacAddress.text.toString(),
            bandAlarmTitle = alarmTitle.text.toString().ifBlank { SettingsStore.DEFAULT_ALARM_TITLE },
            setAndroidClockAlarm = setAndroidAlarm.isChecked,
            setBandAlarm = setBandAlarm.isChecked
        )
        store.save(settings)
        updateStatus(settings)
    }

    private fun updateStatus(settings: UserSettings) {
        status.text = buildString {
            append("Gadgetbridge setup:\n")
            append("1. Pair Xiaomi Smart Band 9 in Gadgetbridge.\n")
            append("2. Device settings -> Device actions -> On Fall Asleep -> Send Broadcast.\n")
            append("3. Broadcast message: nodomain.freeyourgadget.gadgetbridge.FellAsleep.\n")
            append("4. For band alarm: Device settings -> Developer settings -> allow 3rd party apps to set alarms.\n\n")
            append("Current sleep duration: ${settings.sleepHours}h ${settings.sleepMinutes}m\n")
            append("Band target: ${if (settings.hasBandTarget) settings.bandMacAddress else "not configured"}")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 9001)
        }
    }
}
