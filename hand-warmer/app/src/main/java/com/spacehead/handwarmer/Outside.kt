package com.spacehead.handwarmer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Outside temperature: from an ambient temperature sensor when the phone has one,
 * otherwise current local weather from Open-Meteo (free, no API key) using a coarse location.
 */
object Outside {

    @Volatile var tempC: Float? = null
        private set
    @Volatile var source: String = "--"
        private set

    private const val REFRESH_MS = 15 * 60_000L
    private var lastFetch = 0L
    @Volatile private var fetching = false
    private val main = Handler(Looper.getMainLooper())

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(e: SensorEvent) {
            tempC = e.values[0]
            source = "SENSOR"
        }
        override fun onAccuracyChanged(s: Sensor?, a: Int) {}
    }

    /** Returns true if an ambient sensor is present (S24 Ultra has none; most phones don't). */
    fun startSensor(context: Context): Boolean {
        val sm = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val s = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) ?: return false
        sm.registerListener(sensorListener, s, SensorManager.SENSOR_DELAY_NORMAL)
        return true
    }

    fun stopSensor(context: Context) {
        (context.getSystemService(Context.SENSOR_SERVICE) as SensorManager).unregisterListener(sensorListener)
    }

    fun hasLocationPermission(context: Context) =
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    /** Fetches weather if stale. [onDone] runs on the main thread. */
    fun refreshWeather(context: Context, force: Boolean = false, onDone: () -> Unit = {}) {
        if (source == "SENSOR" || fetching || !hasLocationPermission(context)) return
        if (!force && SystemClock.elapsedRealtime() - lastFetch < REFRESH_MS && tempC != null) return
        fetching = true
        withLocation(context) { loc ->
            if (loc == null) {
                fetching = false
                source = "NO FIX"
                main.post(onDone)
                return@withLocation
            }
            Thread {
                try {
                    val url = URL(
                        "https://api.open-meteo.com/v1/forecast?latitude=%.3f&longitude=%.3f&current=temperature_2m"
                            .format(java.util.Locale.US, loc.latitude, loc.longitude)
                    )
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 8_000
                    conn.readTimeout = 8_000
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    conn.disconnect()
                    tempC = JSONObject(body).getJSONObject("current").getDouble("temperature_2m").toFloat()
                    source = "WEATHER"
                    lastFetch = SystemClock.elapsedRealtime()
                } catch (_: Exception) {
                    if (tempC == null) source = "OFFLINE"
                } finally {
                    fetching = false
                    main.post(onDone)
                }
            }.start()
        }
    }

    @Suppress("MissingPermission")
    private fun withLocation(context: Context, cb: (Location?) -> Unit) {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val known = listOf(LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER, LocationManager.GPS_PROVIDER)
            .mapNotNull { runCatching { lm.getLastKnownLocation(it) }.getOrNull() }
            .maxByOrNull { it.time }
        if (known != null) return cb(known)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            runCatching { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }.getOrDefault(false)
        ) {
            lm.getCurrentLocation(LocationManager.NETWORK_PROVIDER, null, context.mainExecutor) { cb(it) }
        } else {
            cb(null)
        }
    }

    /** Colder outside → warmer target. -5 °C or below → 44 °C, 15 °C or above → 36 °C. Never Overdrive. */
    fun autoTarget(outsideC: Float): Int =
        (44f - (outsideC + 5f) * 0.4f).toInt().coerceIn(36, HeaterService.MAX_NORMAL_TARGET_C)
}
