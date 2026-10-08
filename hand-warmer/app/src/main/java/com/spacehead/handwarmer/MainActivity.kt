package com.spacehead.handwarmer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import com.spacehead.handwarmer.ui.HeatGraphView
import com.spacehead.handwarmer.ui.Palette
import com.spacehead.handwarmer.ui.ThermalGaugeView
import com.spacehead.handwarmer.ui.applySystemBarPadding
import com.spacehead.handwarmer.ui.dp
import com.spacehead.handwarmer.ui.edgeToEdge

class MainActivity : Activity() {

    companion object {
        private const val MIN_TARGET_C = 32
        private val TIMER_STEPS = intArrayOf(10, 15, 20, 30, 45, 60, 90, 120)
        private const val REQ_NOTIFY = 1
        private const val REQ_LOCATION = 2
        private const val REQ_OVERDRIVE = 3

        private const val MODE_MANUAL = "manual"
        private const val MODE_AUTO = "auto"
        private val PRESETS = linkedMapOf("GENTLE" to 36, "WARM" to 40, "BLAZE" to 44)
    }

    private lateinit var gauge: ThermalGaugeView
    private lateinit var graph: HeatGraphView
    private lateinit var statusText: TextView
    private lateinit var stateTag: TextView
    private lateinit var statusDot: View
    private lateinit var targetLabel: TextView
    private lateinit var targetSeek: SeekBar
    private lateinit var timerLabel: TextView
    private lateinit var timerSeek: SeekBar
    private lateinit var turboSwitch: Switch
    private lateinit var screenBoost: Switch
    private lateinit var overdriveSwitch: Switch
    private lateinit var toggleButton: TextView
    private lateinit var interlocks: TextView

    private lateinit var roomVal: TextView
    private lateinit var outsideVal: TextView
    private lateinit var powerVal: TextView
    private lateinit var batteryVal: TextView
    private lateinit var headroomVal: TextView
    private lateinit var thermalVal: TextView
    private lateinit var rateVal: TextView
    private lateinit var peakVal: TextView
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
    /** Never persisted: Overdrive resets every time the app is opened. */
    private var overdrive = false
    /** Suppresses switch listeners while we set switch state from code. */
    private var settingSwitch = false

    private val maxTargetC get() = if (overdrive) HeaterService.MAX_OVERDRIVE_TARGET_C else HeaterService.MAX_NORMAL_TARGET_C
    private val targetC get() = MIN_TARGET_C + targetSeek.progress
    private val minutes: Int
        get() {
            val m = TIMER_STEPS[timerSeek.progress]
            return if (overdrive) m.coerceAtMost(HeaterService.OVERDRIVE_MAX_MINUTES) else m
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdge()
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.content).applySystemBarPadding()

        gauge = findViewById(R.id.gauge)
        graph = findViewById(R.id.graph)
        statusText = findViewById(R.id.statusText)
        stateTag = findViewById(R.id.stateTag)
        statusDot = findViewById(R.id.statusDot)
        targetLabel = findViewById(R.id.targetLabel)
        targetSeek = findViewById(R.id.targetSeek)
        timerLabel = findViewById(R.id.timerLabel)
        timerSeek = findViewById(R.id.timerSeek)
        turboSwitch = findViewById(R.id.turbo)
        screenBoost = findViewById(R.id.screenBoost)
        overdriveSwitch = findViewById(R.id.overdrive)
        toggleButton = findViewById(R.id.toggleButton)
        interlocks = findViewById(R.id.interlocks)

        val row1 = findViewById<LinearLayout>(R.id.row1)
        val row2 = findViewById<LinearLayout>(R.id.row2)
        val row3 = findViewById<LinearLayout>(R.id.row3)
        roomVal = cell(row1, "ROOM")
        outsideVal = cell(row1, "OUTSIDE")
        powerVal = cell(row1, "POWER")
        batteryVal = cell(row2, "BATTERY")
        headroomVal = cell(row2, "HEADROOM")
        thermalVal = cell(row2, "THERMAL")
        rateVal = cell(row3, "RATE")
        peakVal = cell(row3, "PEAK")
        timeVal = cell(row3, "REMAINING")
        (roomVal.parent as View).setOnClickListener { showCalibrateDialog() }
        (outsideVal.parent as View).setOnClickListener { requestOutsideTemp(force = true) }

        val presetRow = findViewById<LinearLayout>(R.id.presetRow)
        PRESETS.forEach { (name, c) -> presetChips[name] = chip(presetRow, name) { selectPreset(c) } }
        presetChips["AUTO"] = chip(presetRow, "AUTO") { selectAuto() }

        // The slider always spans up to the Overdrive max; values above the normal max
        // are only reachable with Overdrive on.
        targetSeek.max = HeaterService.MAX_OVERDRIVE_TARGET_C - MIN_TARGET_C
        targetSeek.progress = prefs.getInt("target", 40).coerceAtMost(HeaterService.MAX_NORMAL_TARGET_C) - MIN_TARGET_C
        timerSeek.max = TIMER_STEPS.size - 1
        timerSeek.progress = prefs.getInt("timerIdx", 3)
        turboSwitch.isChecked = prefs.getBoolean("turbo", false)
        screenBoost.isChecked = prefs.getBoolean("screenBoost", false)
        mode = prefs.getString("mode", MODE_MANUAL) ?: MODE_MANUAL

        val onChange = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) {
                if (s === targetSeek) {
                    if (p > maxTargetC - MIN_TARGET_C) s.progress = maxTargetC - MIN_TARGET_C
                    if (fromUser) mode = MODE_MANUAL
                }
                updateLabels()
            }
            override fun onStartTrackingTouch(s: SeekBar?) {}
            override fun onStopTrackingTouch(s: SeekBar?) = applySettings()
        }
        targetSeek.setOnSeekBarChangeListener(onChange)
        timerSeek.setOnSeekBarChangeListener(onChange)
        turboSwitch.setOnCheckedChangeListener { _, _ -> applySettings() }
        screenBoost.setOnCheckedChangeListener { _, _ ->
            saveSettings()
            applyScreenBoost()
        }
        overdriveSwitch.setOnCheckedChangeListener { _, checked ->
            if (settingSwitch) return@setOnCheckedChangeListener
            if (checked) {
                // Don't flip it on until the user has read and accepted the warning.
                setOverdriveSwitch(false)
                @Suppress("DEPRECATION")
                startActivityForResult(Intent(this, OverdriveActivity::class.java), REQ_OVERDRIVE)
            } else {
                setOverdrive(false)
            }
        }
        toggleButton.setOnClickListener {
            if (HeaterService.isRunning) stopHeater() else startHeater()
        }

        val version = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: ""
        findViewById<TextView>(R.id.buildTag).text = "THERMAL SYSTEMS · BUILD $version"

        hasAmbientSensor = Outside.startSensor(this)
        RoomTemp.schedule(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_OVERDRIVE && resultCode == RESULT_OK) setOverdrive(true)
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
            .putExtra(HeaterService.EXTRA_TURBO, turboSwitch.isChecked)
            .putExtra(HeaterService.EXTRA_OVERDRIVE, overdrive)
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

    private fun setOverdriveSwitch(on: Boolean) {
        settingSwitch = true
        overdriveSwitch.isChecked = on
        settingSwitch = false
    }

    private fun setOverdrive(on: Boolean) {
        overdrive = on
        setOverdriveSwitch(on)
        if (on) {
            mode = MODE_MANUAL
            targetSeek.progress = HeaterService.MAX_OVERDRIVE_TARGET_C - MIN_TARGET_C
        } else if (targetC > HeaterService.MAX_NORMAL_TARGET_C) {
            targetSeek.progress = HeaterService.MAX_NORMAL_TARGET_C - MIN_TARGET_C
        }
        applySettings()
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

    // ---- Outside / room temperature ----------------------------------------

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

    private fun showCalibrateDialog() {
        val est = RoomTemp.estimate(this)
        if (est == null) {
            AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                .setTitle("Room temperature")
                .setMessage("No estimate right now — the phone is heating or charging. Try again once it has been idle for a while.")
                .setPositiveButton("OK", null)
                .show()
            return
        }
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            hint = "e.g. 21.5"
            setTextColor(Palette.TEXT)
        }
        val box = LinearLayout(this).apply {
            setPadding(24.dp.toInt(), 8.dp.toInt(), 24.dp.toInt(), 0)
            addView(input, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
        }
        val how = if (est.idleBased) {
            "Estimated from the phone's temperature while it sat idle (${est.ageMin} min ago)."
        } else {
            "Rough live estimate — it gets better once the phone has been idle with the screen off for a while."
        }
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Room ≈ %.1f °C".format(est.tempC))
            .setMessage("$how\n\nKnow the real room temperature (thermostat, thermometer)? Enter it to calibrate.")
            .setView(box)
            .setPositiveButton("Calibrate") { _, _ ->
                input.text.toString().replace(',', '.').toFloatOrNull()?.let {
                    RoomTemp.calibrate(this, it)
                    refresh()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // ---- Rendering ---------------------------------------------------------

    private fun refresh() {
        val battery = Monitor.readBattery(this)
        val running = HeaterService.isRunning

        // The service records while running; record here otherwise so the graph is always live.
        if (!running && battery != null) Monitor.record(battery.tempC, 0f)

        // Keep the weather fresh (rate-limited inside) and follow it in AUTO mode.
        if ((mode == MODE_AUTO || Outside.tempC != null) && !hasAmbientSensor && Outside.hasLocationPermission(this)) {
            Outside.refreshWeather(this) { if (applyAutoTarget()) applySettings() }
        }

        val od = if (running) HeaterService.overdrive else overdrive
        val limit = if (od) HeaterService.OVERDRIVE_LIMIT_C else HeaterService.NORMAL_LIMIT_C
        gauge.tempC = battery?.tempC
        gauge.targetC = if (running) HeaterService.targetC else targetC.toFloat()
        gauge.duty = HeaterService.duty
        gauge.active = running
        gauge.limitC = limit
        gauge.overdrive = od
        graph.targetC = gauge.targetC
        graph.samples = Monitor.history()

        roomVal.text = RoomTemp.estimate(this)?.let {
            (if (it.idleBased) "~" else "≈") + "%.0f°".format(it.tempC)
        } ?: "--"
        outsideVal.text = Outside.tempC?.let { "%.1f°".format(it) } ?: if (hasAmbientSensor) "…" else "TAP"
        powerVal.text = Monitor.powerWatts(this, battery)?.let { "%.1fW".format(it) } ?: "--"
        batteryVal.text = battery?.let { "${it.percent}%" + if (it.plugged) "+" else "" } ?: "--"
        headroomVal.text = Monitor.headroomPercent(this)?.let { "$it%" } ?: "--"
        thermalVal.text = Monitor.thermalLabel(this)
        rateVal.text = Monitor.ratePerMinute()?.let { "%+.1f°/m".format(it) } ?: "--"
        peakVal.text = if (HeaterService.peakC > 0f) "%.1f°".format(HeaterService.peakC) else "--"

        val accent = if (od) Palette.HEAT_HOT else Palette.HEAT
        if (running) {
            val left = (HeaterService.endsAtElapsed - SystemClock.elapsedRealtime()).coerceAtLeast(0) / 1000
            timeVal.text = "%d:%02d".format(left / 60, left % 60)
            statusText.text = HeaterService.status.uppercase()
            statusText.setTextColor(accent)
            stateTag.text = listOfNotNull("OVERDRIVE".takeIf { od }, "TURBO".takeIf { HeaterService.turbo }, "ACTIVE")
                .joinToString(" · ")
            stateTag.setTextColor(accent)
            statusDot.setBackgroundColor(accent)
            toggleButton.text = "SHUTDOWN"
            toggleButton.setBackgroundResource(R.drawable.btn_stop)
            toggleButton.setTextColor(Palette.HEAT)
        } else {
            timeVal.text = "%d:00".format(minutes)
            statusText.text = (HeaterService.lastStopReason ?: "Standby").uppercase()
            statusText.setTextColor(Palette.DIM)
            stateTag.text = if (od) "OVERDRIVE ARMED" else "IDLE"
            stateTag.setTextColor(if (od) Palette.HEAT_HOT else Palette.DIM)
            statusDot.setBackgroundColor(if (od) Palette.HEAT_HOT else Palette.DIM)
            toggleButton.text = "IGNITE"
            toggleButton.setBackgroundResource(R.drawable.btn_primary)
            toggleButton.setTextColor(Palette.BG)
        }
        interlocks.text = "INTERLOCKS: %.0f°C CUTOFF · PAUSE ON SEVERE HEAT · STOP ON CRITICAL\nPAUSE ON CHARGE · STOP BELOW %d%% BATTERY"
            .format(limit, HeaterService.MIN_BATTERY_PCT)
        applyScreenBoost()
    }

    private fun updateLabels() {
        val auto = mode == MODE_AUTO
        targetLabel.text = when {
            auto -> "TARGET  $targetC°C   [AUTO" + (Outside.tempC?.let { " · OUT %.0f°".format(it) } ?: " · NO DATA") + "]"
            overdrive -> "TARGET  $targetC°C   [OVERDRIVE · MAX ${HeaterService.MAX_OVERDRIVE_TARGET_C}°]"
            else -> "TARGET  $targetC°C   [MAX ${HeaterService.MAX_NORMAL_TARGET_C}°]"
        }
        targetLabel.setTextColor(if (overdrive) Palette.HEAT_HOT else Palette.TEXT)
        timerLabel.text = if (overdrive && TIMER_STEPS[timerSeek.progress] > minutes) {
            "AUTO-OFF  $minutes MIN   [OVERDRIVE CAP]"
        } else {
            "AUTO-OFF  $minutes MIN"
        }
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
            // Persist at most the normal max so the app never opens in an Overdrive target.
            .putInt("target", targetC.coerceAtMost(HeaterService.MAX_NORMAL_TARGET_C))
            .putInt("timerIdx", timerSeek.progress)
            .putBoolean("turbo", turboSwitch.isChecked)
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
