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
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import com.spacehead.handwarmer.ui.HeatGraphView
import com.spacehead.handwarmer.ui.Palette
import com.spacehead.handwarmer.ui.ThermalGaugeView
import com.spacehead.handwarmer.ui.dp

class MainActivity : Activity() {

    companion object {
        private const val MIN_TARGET_C = 32
        private const val MAX_TARGET_C = 42
        private val TIMER_STEPS = intArrayOf(10, 15, 20, 30, 45, 60, 90, 120)
        private const val REQ_NOTIFY = 1
        private const val REQ_LOCATION = 2

        private const val MODE_MANUAL = "manual"
        private const val MODE_AUTO = "auto"
        private val PRESETS = linkedMapOf("GENTLE" to 35, "WARM" to 38, "BLAZE" to 41)
    }

    private lateinit var gauge: ThermalGaugeView
    private lateinit var graph: HeatGraphView
    private lateinit var statusText: TextView
    private lateinit var stateTag: TextView
    private lateinit var statusDot: android.view.View
    private lateinit var targetLabel: TextView
    private lateinit var targetSeek: SeekBar
    private lateinit var timerLabel: TextView
    private lateinit var timerSeek: SeekBar
    private lateinit var screenBoost: Switch
    private lateinit var toggleButton: TextView

    private lateinit var outsideVal: TextView
    private lateinit var powerVal: TextView
    private lateinit var batteryVal: TextView
    private lateinit var headroomVal: TextView
    private lateinit var thermalVal: TextView
    private lateinit var timeVal: TextView
    private val presetChips = mutableMapOf<String, TextView>()

    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            refresh()
            handler.postDelayed(this, 1000)
        }
    }

    private var mode = MODE_MANUAL
    private var hasAmbientSensor = false

    private val targetC get() = MIN_TARGET_C + targetSeek.progress
    private val minutes get() = TIMER_STEPS[timerSeek.progress]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gauge = findViewById(R.id.gauge)
        graph = findViewById(R.id.graph)
        statusText = findViewById(R.id.statusText)
        stateTag = findViewById(R.id.stateTag)
        statusDot = findViewById(R.id.statusDot)
        targetLabel = findViewById(R.id.targetLabel)
        targetSeek = findViewById(R.id.targetSeek)
        timerLabel = findViewById(R.id.timerLabel)
        timerSeek = findViewById(R.id.timerSeek)
        screenBoost = findViewById(R.id.screenBoost)
        toggleButton = findViewById(R.id.toggleButton)

        val row1 = findViewById<LinearLayout>(R.id.row1)
        val row2 = findViewById<LinearLayout>(R.id.row2)
        outsideVal = cell(row1, "OUTSIDE")
        powerVal = cell(row1, "POWER")
        batteryVal = cell(row1, "BATTERY")
        headroomVal = cell(row2, "HEADROOM")
        thermalVal = cell(row2, "THERMAL")
        timeVal = cell(row2, "REMAINING")
        (outsideVal.parent as android.view.View).setOnClickListener { requestOutsideTemp(force = true) }

        val presetRow = findViewById<LinearLayout>(R.id.presetRow)
        PRESETS.forEach { (name, c) -> presetChips[name] = chip(presetRow, name) { selectPreset(c) } }
        presetChips["AUTO"] = chip(presetRow, "AUTO") { selectAuto() }

        targetSeek.max = MAX_TARGET_C - MIN_TARGET_C
        targetSeek.progress = prefs.getInt("target", 38) - MIN_TARGET_C
        timerSeek.max = TIMER_STEPS.size - 1
        timerSeek.progress = prefs.getInt("timerIdx", 3)
        screenBoost.isChecked = prefs.getBoolean("screenBoost", false)
        mode = prefs.getString("mode", MODE_MANUAL) ?: MODE_MANUAL

        val onChange = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                if (fromUser && s === targetSeek) mode = MODE_MANUAL
                updateLabels()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) = applySettings()
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

        hasAmbientSensor = Outside.startSensor(this)
        updateLabels()
        requestNotificationPermission()
        if (mode == MODE_AUTO) requestOutsideTemp(force = false)
        handleAutostart(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleAutostart(intent)
    }

    override fun onResume() {
        super.onResume()
        handler.post(ticker)
    }

    override fun onPause() {
        handler.removeCallbacks(ticker)
        super.onPause()
    }

    override fun onDestroy() {
        Outside.stopSensor(this)
        super.onDestroy()
    }

    private fun handleAutostart(intent: Intent?) {
        if (intent?.getBooleanExtra(WarmerTileService.EXTRA_AUTOSTART, false) == true && !HeaterService.isRunning) {
            intent.removeExtra(WarmerTileService.EXTRA_AUTOSTART)
            startHeater()
        }
    }

    // ---- Heater control ----------------------------------------------------

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

    /** Saves settings and pushes them to a running session. */
    private fun applySettings() {
        saveSettings()
        updateLabels()
        if (HeaterService.isRunning) startHeater()
    }

    private fun selectPreset(c: Int) {
        mode = MODE_MANUAL
        targetSeek.progress = c - MIN_TARGET_C
        applySettings()
    }

    private fun selectAuto() {
        mode = MODE_AUTO
        requestOutsideTemp(force = false)
        applyAutoTarget()
        applySettings()
    }

    /** In AUTO mode the target follows the outside temperature. Returns true if it changed. */
    private fun applyAutoTarget(): Boolean {
        if (mode != MODE_AUTO) return false
        val outside = Outside.tempC ?: return false
        val p = Outside.autoTarget(outside) - MIN_TARGET_C
        if (p == targetSeek.progress) return false
        targetSeek.progress = p
        return true
    }

    // ---- Outside temperature -----------------------------------------------

    private fun requestOutsideTemp(force: Boolean) {
        if (hasAmbientSensor) return
        if (!Outside.hasLocationPermission(this)) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQ_LOCATION)
            return
        }
        Outside.refreshWeather(this, force) {
            if (applyAutoTarget()) applySettings()
            refresh()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOCATION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            requestOutsideTemp(force = true)
        }
    }

    // ---- Rendering ---------------------------------------------------------

    private fun refresh() {
        val battery = Monitor.readBattery(this)
        val running = HeaterService.isRunning

        // The service records while running; record here otherwise so the graph is always live.
        if (!running && battery != null) Monitor.record(battery.tempC, 0f)

        // Keep the weather fresh (rate-limited inside) and follow it in AUTO mode.
        if (mode == MODE_AUTO || Outside.tempC != null) {
            if (!hasAmbientSensor && Outside.hasLocationPermission(this)) {
                Outside.refreshWeather(this) { if (applyAutoTarget()) applySettings() }
            }
        }

        gauge.tempC = battery?.tempC
        gauge.targetC = if (running) HeaterService.targetC else targetC.toFloat()
        gauge.duty = HeaterService.duty
        gauge.active = running
        graph.targetC = gauge.targetC
        graph.samples = Monitor.history()

        outsideVal.text = Outside.tempC?.let { "%.1f°".format(it) } ?: if (hasAmbientSensor) "…" else "TAP"
        powerVal.text = Monitor.powerWatts(this, battery)?.let { "%.1fW".format(it) } ?: "--"
        batteryVal.text = battery?.let { "${it.percent}%" + if (it.plugged) "+" else "" } ?: "--"
        headroomVal.text = Monitor.headroomPercent(this)?.let { "$it%" } ?: "--"
        thermalVal.text = Monitor.thermalLabel(this)

        if (running) {
            val left = (HeaterService.endsAtElapsed - SystemClock.elapsedRealtime()).coerceAtLeast(0) / 1000
            timeVal.text = "%d:%02d".format(left / 60, left % 60)
            statusText.text = HeaterService.status.uppercase()
            statusText.setTextColor(Palette.HEAT)
            stateTag.text = "ACTIVE"
            stateTag.setTextColor(Palette.HEAT)
            statusDot.setBackgroundColor(Palette.HEAT)
            toggleButton.text = "SHUTDOWN"
            toggleButton.setBackgroundResource(R.drawable.btn_stop)
            toggleButton.setTextColor(Palette.HEAT)
        } else {
            timeVal.text = "%d:00".format(minutes)
            statusText.text = (HeaterService.lastStopReason ?: "Standby").uppercase()
            statusText.setTextColor(Palette.DIM)
            stateTag.text = "IDLE"
            stateTag.setTextColor(Palette.DIM)
            statusDot.setBackgroundColor(Palette.DIM)
            toggleButton.text = "IGNITE"
            toggleButton.setBackgroundResource(R.drawable.btn_primary)
            toggleButton.setTextColor(Palette.BG)
        }
        applyScreenBoost()
    }

    private fun updateLabels() {
        val auto = mode == MODE_AUTO
        targetLabel.text = if (auto) {
            "TARGET  $targetC°C   [AUTO" + (Outside.tempC?.let { " · OUT %.0f°".format(it) } ?: " · NO DATA") + "]"
        } else {
            "TARGET  $targetC°C"
        }
        timerLabel.text = "AUTO-OFF  $minutes MIN"
        presetChips.forEach { (name, v) ->
            v.isSelected = if (auto) name == "AUTO" else PRESETS[name] == targetC
        }
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

    private fun saveSettings() {
        prefs.edit()
            .putInt("target", targetC)
            .putInt("timerIdx", timerSeek.progress)
            .putBoolean("screenBoost", screenBoost.isChecked)
            .putString("mode", mode)
            .apply()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQ_NOTIFY)
        }
    }

    // ---- View builders -----------------------------------------------------

    /** Adds a telemetry cell (dim label over a value) and returns the value view. */
    private fun cell(row: LinearLayout, label: String): TextView {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(TextView(this).apply {
            text = label
            textSize = 9f
            letterSpacing = 0.25f
            setTextColor(Palette.DIM)
        })
        val value = TextView(this).apply {
            text = "--"
            textSize = 17f
            setTextColor(Palette.TEXT)
            setPadding(0, 4.dp.toInt(), 0, 0)
        }
        box.addView(value)
        row.addView(box, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return value
    }

    private fun chip(row: LinearLayout, label: String, onClick: () -> Unit): TextView {
        val v = TextView(this).apply {
            text = label
            textSize = 11f
            letterSpacing = 0.2f
            gravity = Gravity.CENTER
            setTextColor(Palette.TEXT)
            setBackgroundResource(R.drawable.chip)
            setOnClickListener { onClick() }
        }
        val lp = LinearLayout.LayoutParams(0, 40.dp.toInt(), 1f)
        if (row.childCount > 0) lp.marginStart = 8.dp.toInt()
        row.addView(v, lp)
        return v
    }
}
