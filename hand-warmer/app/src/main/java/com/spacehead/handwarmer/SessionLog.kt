package com.spacehead.handwarmer

import android.os.SystemClock
import java.io.File

/**
 * Per-session diagnostics: one row per control tick, exportable as CSV so a real
 * session on a real phone can be analysed (why it plateaued, paused, cooled, ...).
 */
object SessionLog {

    data class Row(
        val tSec: Int,
        val tempC: Float,
        val targetC: Float,
        val duty: Float,
        val thermal: Int,
        val headroomPct: Int?,
        val powerW: Float?,
        val plugged: Boolean,
        val screenOn: Boolean,
        val cpuMaxMhz: Int?,
        val gpuDps: Int?,
        val status: String,
    )

    private const val MAX_ROWS = 3600 // 2 h at one row per 2 s
    private val rows = ArrayList<Row>()
    private var startElapsed = 0L

    @Synchronized
    fun start() {
        rows.clear()
        startElapsed = SystemClock.elapsedRealtime()
    }

    @Synchronized
    fun add(
        tempC: Float, targetC: Float, duty: Float, thermal: Int, headroomPct: Int?,
        powerW: Float?, plugged: Boolean, screenOn: Boolean, gpuDps: Int?, status: String,
    ) {
        if (rows.size >= MAX_ROWS) rows.removeAt(0)
        val t = ((SystemClock.elapsedRealtime() - startElapsed) / 1000).toInt()
        rows += Row(t, tempC, targetC, duty, thermal, headroomPct, powerW, plugged, screenOn, cpuMaxMhz(), gpuDps, status)
    }

    @Synchronized
    fun isEmpty() = rows.isEmpty()

    @Synchronized
    fun toCsv(header: String): String = buildString {
        appendLine(header)
        appendLine("t_s,battery_c,target_c,duty_pct,thermal_status,headroom_pct,power_w,plugged,screen_on,cpu_max_mhz,gpu_mpix_per_s,status")
        // Keep the share intent well under Android's ~1 MB binder limit.
        for (r in rows.takeLast(1800)) {
            appendLine(
                listOf(
                    r.tSec, "%.1f".format(r.tempC), "%.0f".format(r.targetC), (r.duty * 100).toInt(),
                    r.thermal, r.headroomPct ?: "", r.powerW?.let { "%.2f".format(it) } ?: "",
                    if (r.plugged) 1 else 0, if (r.screenOn) 1 else 0, r.cpuMaxMhz ?: "", r.gpuDps ?: "", r.status.replace(',', ';'),
                ).joinToString(",")
            )
        }
    }

    /** Highest current CPU frequency across cores, if the kernel lets apps read it. */
    private fun cpuMaxMhz(): Int? {
        var best = -1
        for (i in 0 until Runtime.getRuntime().availableProcessors()) {
            val khz = runCatching {
                File("/sys/devices/system/cpu/cpu$i/cpufreq/scaling_cur_freq").readText().trim().toInt()
            }.getOrNull() ?: continue
            if (khz > best) best = khz
        }
        return if (best > 0) best / 1000 else null
    }
}
