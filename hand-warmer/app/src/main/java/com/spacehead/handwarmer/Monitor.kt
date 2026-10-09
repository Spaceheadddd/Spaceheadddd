package com.spacehead.handwarmer

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import kotlin.math.abs

/** Live readings and a rolling temperature history shared by the service and the UI. */
object Monitor {

    data class BatteryInfo(val tempC: Float, val percent: Int, val plugged: Boolean, val voltageMv: Int)
    data class Sample(val atElapsed: Long, val tempC: Float, val duty: Float)

    /** How much history the graph shows. */
    const val WINDOW_MS = 12 * 60_000L
    private const val MIN_SPACING_MS = 1_500L

    private val samples = ArrayDeque<Sample>()

    @Synchronized
    fun record(tempC: Float, duty: Float) {
        val now = SystemClock.elapsedRealtime()
        val last = samples.lastOrNull()
        if (last != null && now - last.atElapsed < MIN_SPACING_MS) return
        samples.addLast(Sample(now, tempC, duty))
        while (samples.isNotEmpty() && now - samples.first().atElapsed > WINDOW_MS) samples.removeFirst()
    }

    @Synchronized
    fun history(): List<Sample> = samples.toList()

    /**
     * Warming rate in °C/min: least-squares slope over the last 3 minutes. Samsung updates
     * battery temperature in coarse steps every 30–90 s, so a short window reads as 0.
     */
    @Synchronized
    fun ratePerMinute(): Float? {
        val last = samples.lastOrNull() ?: return null
        val window = samples.filter { last.atElapsed - it.atElapsed <= 180_000L }
        if (window.size < 3 || last.atElapsed - window.first().atElapsed < 60_000L) return null
        val t0 = window.first().atElapsed
        val xs = window.map { (it.atElapsed - t0) / 60_000.0 }
        val ys = window.map { it.tempC.toDouble() }
        val mx = xs.average()
        val my = ys.average()
        var num = 0.0
        var den = 0.0
        for (i in xs.indices) {
            num += (xs[i] - mx) * (ys[i] - my)
            den += (xs[i] - mx) * (xs[i] - mx)
        }
        return if (den > 0.0) (num / den).toFloat() else null
    }

    fun readBattery(context: Context): BatteryInfo? {
        val i = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return null
        val tenths = i.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)
        if (tenths == Int.MIN_VALUE) return null
        val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val plugged = i.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) != 0
        val voltage = i.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val pct = if (level >= 0 && scale > 0) level * 100 / scale else 100
        return BatteryInfo(tenths / 10f, pct, plugged, voltage)
    }

    /** Instantaneous battery power in watts, or null if the device doesn't report current. */
    fun powerWatts(context: Context, battery: BatteryInfo?): Float? {
        if (battery == null || battery.voltageMv <= 0) return null
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val raw = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        if (raw == 0 || raw == Int.MIN_VALUE) return null
        // Spec says microamps, but some Samsung firmwares report milliamps.
        val amps = if (abs(raw) < 20_000) abs(raw) / 1_000f else abs(raw) / 1_000_000f
        return amps * battery.voltageMv / 1_000f
    }

    /**
     * Android's forecast of thermal headroom: 0 = cool, 1 = throttling begins.
     * Returned as a percentage remaining (100% = lots of room), or null if unsupported.
     */
    fun headroomPercent(context: Context): Int? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return null
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val h = pm.getThermalHeadroom(10)
        if (h.isNaN()) return null
        return ((1f - h) * 100).toInt().coerceIn(0, 100)
    }

    fun thermalLabel(context: Context): String {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return when (pm.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_NONE -> "NOMINAL"
            PowerManager.THERMAL_STATUS_LIGHT -> "LIGHT"
            PowerManager.THERMAL_STATUS_MODERATE -> "MODERATE"
            PowerManager.THERMAL_STATUS_SEVERE -> "SEVERE"
            else -> "CRITICAL"
        }
    }
}
