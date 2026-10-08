package com.spacehead.handwarmer

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.PowerManager

/**
 * Room temperature estimated from the phone alone (no ambient sensor on the S24 Ultra).
 *
 * A phone that has been sitting idle — screen off, not charging, not heating — settles a
 * little above the air around it. A background job samples the battery temperature in
 * that state every ~15 minutes; the estimate is the coolest recent sample minus an offset.
 * The user can calibrate the offset once against a real thermometer.
 */
object RoomTemp {

    data class Estimate(val tempC: Float, val ageMin: Int, val idleBased: Boolean)

    private const val PREFS = "room"
    private const val JOB_ID = 42
    private const val DEFAULT_OFFSET_C = 1.5f
    /** Rough fallback when there's no idle sample: an in-use phone runs warmer. */
    private const val LIVE_OFFSET_C = 3.5f
    private const val SAMPLE_WINDOW_MS = 90 * 60_000L
    private const val MAX_AGE_MS = 6 * 60 * 60_000L
    private const val COOLDOWN_AFTER_HEAT_MS = 30 * 60_000L

    fun schedule(context: Context) {
        val js = context.getSystemService(JobScheduler::class.java)
        if (js.getPendingJob(JOB_ID) != null) return
        js.schedule(
            JobInfo.Builder(JOB_ID, ComponentName(context, RoomTempJob::class.java))
                .setPeriodic(15 * 60_000L)
                .setPersisted(true)
                .build()
        )
    }

    fun markHeatEnded(context: Context) {
        prefs(context).edit().putLong("heatEnded", System.currentTimeMillis()).apply()
    }

    /** Takes a sample if the phone is in a state that reflects the room. */
    fun sample(context: Context) {
        val battery = Monitor.readBattery(context) ?: return
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val p = prefs(context)
        val now = System.currentTimeMillis()
        if (battery.plugged || pm.isInteractive || HeaterService.isRunning) return
        if (now - p.getLong("heatEnded", 0L) < COOLDOWN_AFTER_HEAT_MS) return

        val kept = readSamples(context).filter { now - it.first < MAX_AGE_MS }.takeLast(23)
        val all = kept + (now to battery.tempC)
        p.edit().putString("samples", all.joinToString(";") { "${it.first},${it.second}" }).apply()
    }

    fun estimate(context: Context): Estimate? {
        val now = System.currentTimeMillis()
        val offset = offset(context)
        val recent = readSamples(context).filter { now - it.first < MAX_AGE_MS }
        if (recent.isNotEmpty()) {
            val newest = recent.maxOf { it.first }
            // Coolest reading in the 90 min leading up to the newest one.
            val window = recent.filter { newest - it.first <= SAMPLE_WINDOW_MS }
            val raw = window.minOf { it.second }
            return Estimate(raw - offset, ((now - newest) / 60_000L).toInt(), idleBased = true)
        }
        if (HeaterService.isRunning) return null
        val battery = Monitor.readBattery(context) ?: return null
        if (battery.plugged) return null
        return Estimate(battery.tempC - LIVE_OFFSET_C, 0, idleBased = false)
    }

    /** Sets the offset so the current estimate matches a real reading. */
    fun calibrate(context: Context, actualC: Float) {
        val est = estimate(context) ?: return
        val raw = est.tempC + (if (est.idleBased) offset(context) else LIVE_OFFSET_C)
        val newOffset = (raw - actualC).coerceIn(-2f, 8f)
        prefs(context).edit().putFloat("offset", newOffset).putBoolean("calibrated", true).apply()
    }

    fun isCalibrated(context: Context) = prefs(context).getBoolean("calibrated", false)

    private fun offset(context: Context) = prefs(context).getFloat("offset", DEFAULT_OFFSET_C)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun readSamples(context: Context): List<Pair<Long, Float>> =
        prefs(context).getString("samples", "")!!
            .split(';')
            .mapNotNull { s ->
                val parts = s.split(',')
                if (parts.size != 2) null
                else parts[0].toLongOrNull()?.let { t -> parts[1].toFloatOrNull()?.let { t to it } }
            }
}

class RoomTempJob : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        RoomTemp.sample(this)
        return false
    }

    override fun onStopJob(params: JobParameters?) = false
}
