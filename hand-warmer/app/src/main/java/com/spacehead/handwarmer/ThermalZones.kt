package com.spacehead.handwarmer

import java.io.File

/**
 * Reads the kernel's thermal sensors (CPU, GPU, skin, ...) where the phone lets apps read
 * them. The battery sensor lags and jumps; these show the hotspot your hand actually feels.
 * Which zones are readable varies by device and Android version, so every read is optional.
 */
object ThermalZones {

    data class Zone(val name: String, val tempC: Float)

    private var readable: List<Pair<String, File>>? = null

    /** Finds readable zones once; later reads only open those files. */
    @Synchronized
    private fun zones(): List<Pair<String, File>> {
        readable?.let { return it }
        val found = mutableListOf<Pair<String, File>>()
        for (i in 0 until 160) {
            val dir = File("/sys/class/thermal/thermal_zone$i")
            if (!dir.exists()) continue
            val type = runCatching { File(dir, "type").readText().trim() }.getOrNull() ?: continue
            val temp = File(dir, "temp")
            if (parse(runCatching { temp.readText() }.getOrNull()) != null) found += type to temp
        }
        readable = found
        return found
    }

    private fun parse(raw: String?): Float? {
        val v = raw?.trim()?.toFloatOrNull() ?: return null
        // Most kernels report millidegrees; some report whole degrees.
        val c = if (v > 1000f || v < -1000f) v / 1000f else v
        return c.takeIf { it in 5f..130f }
    }

    fun readAll(): List<Zone> =
        zones().mapNotNull { (name, file) -> parse(runCatching { file.readText() }.getOrNull())?.let { Zone(name, it) } }

    /** Hottest readable sensor, ignoring the battery and charger zones. */
    fun hottest(): Zone? = readAll()
        .filterNot { it.name.contains("batt", true) || it.name.contains("chg", true) || it.name.contains("usb", true) }
        .maxByOrNull { it.tempC }

    /** A skin / case sensor if the phone exposes one: closest to what your hand feels. */
    fun skin(): Zone? = readAll().filter { it.name.contains("skin", true) }.maxByOrNull { it.tempC }

    fun readableCount() = zones().size
}
