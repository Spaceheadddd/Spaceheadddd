package com.spacehead.handwarmer

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : Activity() {

    companion object {
        private const val MIN_TARGET_C = 32
        private const val MAX_TARGET_C = 42
        private val TIMER_STEPS = intArrayOf(10, 15, 20, 30, 45, 60, 90, 120)
    }

    private lateinit var tempText: TextView
    private lateinit var statusText: TextView
    private lateinit var targetLabel: TextView
    private lateinit var targetSeek: SeekBar
    private lateinit var timerLabel: TextView
    private lateinit var timerSeek: SeekBar
    private lateinit var screenBoost: CheckBox
    private lateinit var toggleButton: Button
    private lateinit var flame: ImageView

    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            refresh()
            handler.postDelayed(this, 1000)
        }
    }

    private val targetC get() = MIN_TARGET_C + targetSeek.progress
    private val minutes get() = TIMER_STEPS[timerSeek.progress]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tempText = findViewById(R.id.tempText)
        statusText = findViewById(R.id.statusText)
        targetLabel = findViewById(R.id.targetLabel)
        targetSeek = findViewById(R.id.targetSeek)
        timerLabel = findViewById(R.id.timerLabel)
        timerSeek = findViewById(R.id.timerSeek)
        screenBoost = findViewById(R.id.screenBoost)
        toggleButton = findViewById(R.id.toggleButton)
        flame = findViewById(R.id.flame)

        targetSeek.max = MAX_TARGET_C - MIN_TARGET_C
        targetSeek.progress = prefs.getInt("target", 38) - MIN_TARGET_C
        timerSeek.max = TIMER_STEPS.size - 1
        timerSeek.progress = prefs.getInt("timerIdx", 3)
        screenBoost.isChecked = prefs.getBoolean("screenBoost", false)

        val onChange = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) = updateLabels()
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) {
                saveSettings()
                // Apply new settings to a running session.
                if (HeaterService.isRunning) startHeater()
            }
        }
        targetSeek.setOnSeekBarChangeListener(onChange)
        timerSeek.setOnSeekBarChangeListener(onChange)
        screenBoost.setOnCheckedChangeListener { _, _ ->
            saveSettings()
            applyScreenBoost()
        }

        toggleButton.setOnClickListener {
            if (HeaterService.isRunning) stopHeater() else startHeater()
        }

        updateLabels()
        requestNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        handler.post(ticker)
    }

    override fun onPause() {
        handler.removeCallbacks(ticker)
        super.onPause()
    }

    private fun startHeater() {
        val intent = Intent(this, HeaterService::class.java)
            .setAction(HeaterService.ACTION_START)
            .putExtra(HeaterService.EXTRA_TARGET_C, targetC.toFloat())
            .putExtra(HeaterService.EXTRA_MINUTES, minutes)
        startForegroundService(intent)
        handler.postDelayed({ refresh() }, 300)
    }

    private fun stopHeater() {
        startService(Intent(this, HeaterService::class.java).setAction(HeaterService.ACTION_STOP))
        handler.postDelayed({ refresh() }, 300)
    }

    private fun refresh() {
        val battery = HeaterService.readBattery(this)
        tempText.text = battery?.let { "%.1f °C".format(it.tempC) } ?: "--.- °C"

        val running = HeaterService.isRunning
        statusText.text = if (running) {
            val minsLeft = ((HeaterService.endsAtElapsed - SystemClock.elapsedRealtime()) / 60_000L) + 1
            "${HeaterService.status}\n$minsLeft min left"
        } else {
            HeaterService.lastStopReason ?: "Off"
        }
        toggleButton.text = if (running) "Stop" else "Start warming"
        flame.alpha = if (running) 0.45f + 0.55f * HeaterService.duty else 0.35f
        applyScreenBoost()
    }

    /** Keeps the screen on at full brightness for extra heat, only while warming and visible. */
    private fun applyScreenBoost() {
        val on = screenBoost.isChecked && HeaterService.isRunning
        val lp = window.attributes
        val brightness = if (on) 1f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        if (lp.screenBrightness != brightness) {
            lp.screenBrightness = brightness
            window.attributes = lp
        }
        if (on) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun updateLabels() {
        targetLabel.text = "Target warmth: $targetC °C"
        timerLabel.text = "Auto-off after: $minutes min"
    }

    private fun saveSettings() {
        prefs.edit()
            .putInt("target", targetC)
            .putInt("timerIdx", timerSeek.progress)
            .putBoolean("screenBoost", screenBoost.isChecked)
            .apply()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        }
    }
}
