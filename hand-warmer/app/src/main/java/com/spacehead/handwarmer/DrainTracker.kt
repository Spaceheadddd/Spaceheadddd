package com.spacehead.handwarmer

import android.content.Context
import android.os.BatteryManager
import org.json.JSONArray
import org.json.JSONObject

/**
 * Battery drain accounting.
 *
 * Total drain comes from live battery power. Ignis's share is total minus a learned
 * "normal" baseline for the same screen state (screen-on idle is learned while the app is
 * open and not heating; screen-off idle by the background job). Each session is stored
 * with its mode so modes can be compared against each other and against normal use.
 */
object DrainTracker {

    data class Session(
        val startedAt: Long,
        val durationSec: Int,
        val mode: String,
        val avgW: Float,
        val ignisW: Float,
        val percentUsed: Float,
        val mahUsed: Int?,
        val peakC: Float,
    ) {
        val percentPerHour get() = if (durationSec > 0) percentUsed * 3600f / durationSec else 0f
    }

    data class ModeStats(val mode: String, val sessions: Int, val percentPerHour: Float, val ignisShare: Float)

    private const val PREFS = "drain"
    private const val MAX_SESSIONS = 40
    private const val MIN_SESSION_SEC = 120
    private const val NOMINAL_V = 3.88f
    private const val FALLBACK_MAH = 5000

    // ---- Baselines ----------------------------------------------------------

    /** Learns normal drain. Call only when not heating and not charging. */
    fun noteBaseline(context: Context, powerW: Float, screenOn: Boolean) {
        if (powerW <= 0f || powerW > 20f) return
        val key = if (screenOn) "baseOn" else "baseOff"
        val p = prefs(context)
        val old = p.getFloat(key, -1f)
        // Screen-on samples arrive every second, screen-off ones every ~15 min.
        val alpha = if (screenOn) 0.02f else 0.25f
        val v = if (old < 0f) powerW else old + alpha * (powerW - old)
        p.edit().putFloat(key, v).apply()
    }

    fun baselineW(context: Context, screenOn: Boolean): Float? =
        prefs(context).getFloat(if (screenOn) "baseOn" else "baseOff", -1f).takeIf { it > 0f }

    // ---- Conversions ----------------------------------------------------------

    /** Battery capacity estimate from the charge counter, falling back to 5000 mAh. */
    fun capacityMah(context: Context): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val counter = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        if (counter > 0 && level in 10..100) {
            val mah = counter / 1000f / (level / 100f)
            if (mah in 1500f..8000f) return mah.toInt()
        }
        return FALLBACK_MAH
    }

    fun chargeCounterMah(context: Context): Int? {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val c = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        return if (c > 0) c / 1000 else null
    }

    fun percentPerHour(context: Context, watts: Float): Float =
        watts / (capacityMah(context) * NOMINAL_V / 1000f) * 100f

    // ---- Live session (driven by HeaterService) -----------------------------------

    @Volatile var liveEnergyWh = 0f
        private set
    @Volatile var liveIgnisWh = 0f
        private set
    @Volatile var liveSeconds = 0f
        private set
    private var startedAt = 0L
    private var startCounterMah: Int? = null
    private var startLevel = 0

    @Synchronized
    fun startSession(context: Context) {
        liveEnergyWh = 0f
        liveIgnisWh = 0f
        liveSeconds = 0f
        startedAt = System.currentTimeMillis()
        startCounterMah = chargeCounterMah(context)
        startLevel = Monitor.readBattery(context)?.percent ?: 0
    }

    /** Adds one control tick. Ticks while charging are skipped. */
    @Synchronized
    fun tick(context: Context, powerW: Float?, screenOn: Boolean, plugged: Boolean, dtSec: Float) {
        if (plugged || powerW == null || dtSec <= 0f || dtSec > 30f) return
        val base = baselineW(context, screenOn) ?: 0f
        liveEnergyWh += powerW * dtSec / 3600f
        liveIgnisWh += (powerW - base).coerceAtLeast(0f) * dtSec / 3600f
        liveSeconds += dtSec
    }

    /** Ignis's share of this session's drain, 0..1. */
    fun liveIgnisShare(): Float = if (liveEnergyWh > 0f) liveIgnisWh / liveEnergyWh else 0f

    fun livePercentUsed(context: Context): Float =
        liveEnergyWh / (capacityMah(context) * NOMINAL_V / 1000f) * 100f

    @Synchronized
    fun endSession(context: Context, mode: String, peakC: Float) {
        val sec = liveSeconds.toInt()
        val energy = liveEnergyWh
        val ignis = liveIgnisWh
        // Reset so a second end (e.g. stop after a mode switch) can't record twice.
        liveEnergyWh = 0f
        liveIgnisWh = 0f
        liveSeconds = 0f
        if (sec < MIN_SESSION_SEC || energy <= 0f) return
        val counterNow = chargeCounterMah(context)
        val mah = startCounterMah?.let { s -> counterNow?.let { s - it } }?.takeIf { it > 0 }
        val pct = mah?.let { it * 100f / capacityMah(context) }
            ?: (energy / (capacityMah(context) * NOMINAL_V / 1000f) * 100f)
        val s = Session(
            startedAt, sec, mode,
            avgW = energy * 3600f / sec,
            ignisW = ignis * 3600f / sec,
            percentUsed = pct, mahUsed = mah, peakC = peakC,
        )
        val all = (sessions(context) + s).takeLast(MAX_SESSIONS)
        save(context, all)
    }

    // ---- History ------------------------------------------------------------------

    fun sessions(context: Context): List<Session> {
        val raw = prefs(context).getString("sessions", null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Session(
                    o.getLong("t"), o.getInt("dur"), o.getString("mode"),
                    o.getDouble("avgW").toFloat(), o.getDouble("ignisW").toFloat(),
                    o.getDouble("pct").toFloat(), if (o.has("mah")) o.getInt("mah") else null,
                    o.getDouble("peak").toFloat(),
                )
            }
        }.getOrDefault(emptyList())
    }

    /** Measured drain per mode, weighted by session length. */
    fun modeStats(context: Context): List<ModeStats> =
        sessions(context).groupBy { it.mode }.map { (mode, list) ->
            val totalSec = list.sumOf { it.durationSec }.toFloat()
            val pctPerH = list.sumOf { it.percentUsed.toDouble() }.toFloat() * 3600f / totalSec
            val energy = list.sumOf { (it.avgW * it.durationSec).toDouble() }.toFloat()
            val ignis = list.sumOf { (it.ignisW * it.durationSec).toDouble() }.toFloat()
            ModeStats(mode, list.size, pctPerH, if (energy > 0f) ignis / energy else 0f)
        }.sortedBy { it.percentPerHour }

    fun clearHistory(context: Context) {
        prefs(context).edit().remove("sessions").apply()
    }

    private fun save(context: Context, list: List<Session>) {
        val arr = JSONArray()
        list.forEach { s ->
            arr.put(JSONObject().apply {
                put("t", s.startedAt); put("dur", s.durationSec); put("mode", s.mode)
                put("avgW", s.avgW.toDouble()); put("ignisW", s.ignisW.toDouble())
                put("pct", s.percentUsed.toDouble()); s.mahUsed?.let { put("mah", it) }
                put("peak", s.peakC.toDouble())
            })
        }
        prefs(context).edit().putString("sessions", arr.toString()).apply()
    }

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
