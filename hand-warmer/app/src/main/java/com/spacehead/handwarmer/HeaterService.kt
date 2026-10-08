package com.spacehead.handwarmer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.os.Process
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Foreground service that warms the phone by keeping CPU cores (and optionally the GPU) busy.
 *
 * Heat output is regulated like a thermostat: workers run a duty cycle (busy for part of
 * every 100 ms window, sleep for the rest) and a control loop sets that duty from the
 * battery temperature and how fast it is rising, so the phone heats at full power and
 * then settles at the target instead of overshooting.
 */
class HeaterService : Service() {

    companion object {
        const val ACTION_START = "com.spacehead.handwarmer.START"
        const val ACTION_STOP = "com.spacehead.handwarmer.STOP"
        const val EXTRA_TARGET_C = "target_c"
        const val EXTRA_MINUTES = "minutes"
        const val EXTRA_TURBO = "turbo"
        const val EXTRA_OVERDRIVE = "overdrive"

        /** Highest target without Overdrive, and the cutoff that goes with it. */
        const val MAX_NORMAL_TARGET_C = 44
        const val NORMAL_LIMIT_C = 46f
        /** Overdrive: higher target, higher cutoff, shorter sessions. */
        const val MAX_OVERDRIVE_TARGET_C = 46
        const val OVERDRIVE_LIMIT_C = 48f
        const val OVERDRIVE_MAX_MINUTES = 20
        /** Battery level below which heating stops (when not charging). */
        const val MIN_BATTERY_PCT = 15

        private const val CHANNEL_ID = "heater"
        private const val NOTIFICATION_ID = 1
        private const val CONTROL_INTERVAL_MS = 2000L
        private const val WINDOW_MS = 100L
        /** Battery temperature lags the chip; look this far ahead along the current trend. */
        private const val LEAD_SECONDS = 45f

        // State shared with the UI. Written on the main thread, read by workers/UI.
        @Volatile var isRunning = false
            private set
        @Volatile var duty = 0f
            private set
        @Volatile var status = "Off"
            private set
        @Volatile var endsAtElapsed = 0L
            private set
        /** Why the last session ended, shown in the UI after it stops. */
        @Volatile var lastStopReason: String? = null
            private set
        @Volatile var targetC = 40f
            private set
        @Volatile var turbo = false
            private set
        @Volatile var overdrive = false
            private set
        @Volatile var limitC = NORMAL_LIMIT_C
            private set
        /** Highest battery temperature seen this session. */
        @Volatile var peakC = 0f
            private set
    }

    private val handler = Handler(Looper.getMainLooper())
    private val workers = mutableListOf<Thread>()
    private var gpu: GpuBurner? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var reachedTarget = false
    @Volatile private var active = false

    private val controlLoop = object : Runnable {
        override fun run() {
            if (!active) return
            regulate()
            if (active) handler.postDelayed(this, CONTROL_INTERVAL_MS)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            shutdown("Stopped")
            return START_NOT_STICKY
        }

        overdrive = intent?.getBooleanExtra(EXTRA_OVERDRIVE, false) ?: false
        val maxTarget = if (overdrive) MAX_OVERDRIVE_TARGET_C else MAX_NORMAL_TARGET_C
        val newTarget = (intent?.getFloatExtra(EXTRA_TARGET_C, 40f) ?: 40f).coerceAtMost(maxTarget.toFloat())
        if (newTarget > targetC) reachedTarget = false
        targetC = newTarget
        limitC = if (overdrive) OVERDRIVE_LIMIT_C else NORMAL_LIMIT_C
        turbo = intent?.getBooleanExtra(EXTRA_TURBO, false) ?: false

        var minutes = intent?.getIntExtra(EXTRA_MINUTES, 30) ?: 30
        if (overdrive) minutes = minutes.coerceAtMost(OVERDRIVE_MAX_MINUTES)
        endsAtElapsed = SystemClock.elapsedRealtime() + minutes * 60_000L

        startInForeground(buildNotification("Starting…"))

        if (!active) {
            active = true
            isRunning = true
            lastStopReason = null
            reachedTarget = false
            peakC = 0f
            acquireWakeLock(minutes)
            startWorkers()
            handler.post(controlLoop)
        } else {
            // Already running: settings were updated, re-arm the wake lock for the new duration.
            wakeLock?.let { if (it.isHeld) it.release() }
            acquireWakeLock(minutes)
            regulate()
        }
        syncGpu()
        return START_NOT_STICKY
    }

    private fun regulate() {
        val battery = Monitor.readBattery(this)
        if (battery == null) {
            shutdown("Stopped: can't read battery temperature")
            return
        }
        val temp = battery.tempC
        if (temp > peakC) peakC = temp
        val thermal = (getSystemService(POWER_SERVICE) as PowerManager).currentThermalStatus

        when {
            temp >= limitC ->
                return shutdown("Stopped: phone reached %.1f °C".format(temp))
            thermal >= PowerManager.THERMAL_STATUS_CRITICAL ->
                return shutdown("Stopped: Android reported critical heat")
            !battery.plugged && battery.percent < MIN_BATTERY_PCT ->
                return shutdown("Stopped: battery below $MIN_BATTERY_PCT%")
            SystemClock.elapsedRealtime() >= endsAtElapsed ->
                return shutdown("Timer finished")
        }

        when {
            battery.plugged -> {
                // Heat + charging is the worst combination for battery health.
                duty = 0f
                status = "Paused while charging"
            }
            thermal >= PowerManager.THERMAL_STATUS_SEVERE -> {
                // Let the system cool down; resumes on its own once the status drops.
                duty = 0f
                status = "Cooling: system thermal limit"
            }
            else -> {
                // Proportional control on a predicted temperature: battery temp lags the
                // chip, so project the current trend forward to stop heating in time.
                val ratePerSec = (Monitor.ratePerMinute() ?: 0f) / 60f
                val predicted = temp + (ratePerSec * LEAD_SECONDS).coerceIn(-1f, 1.5f)
                val d = ((targetC + 0.3f - predicted) / 0.8f).coerceIn(0f, 1f)
                duty = d
                if (!reachedTarget && temp >= targetC - 0.3f) {
                    reachedTarget = true
                    buzz()
                }
                status = when {
                    d >= 0.95f -> "Heating up"
                    d <= 0.01f -> "At temperature, resting"
                    else -> "Holding warm (%d%% power)".format((d * 100).toInt())
                }
            }
        }

        Monitor.record(temp, duty)
        WarmerTileService.refresh(this)

        val minsLeft = ((endsAtElapsed - SystemClock.elapsedRealtime()) / 60_000L) + 1
        val tags = listOfNotNull("OVERDRIVE".takeIf { overdrive }, "TURBO".takeIf { turbo }).joinToString(" ")
        notificationManager().notify(
            NOTIFICATION_ID,
            buildNotification(
                "%.1f °C → %.0f °C · %s · %d min left %s".format(temp, targetC, status, minsLeft, tags).trim()
            )
        )
    }

    private fun startWorkers() {
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        repeat(cores) { idx ->
            val t = Thread({ burn() }, "heater-$idx")
            t.isDaemon = true
            workers += t
            t.start()
        }
    }

    private fun syncGpu() {
        if (turbo && active && gpu == null) {
            gpu = GpuBurner { duty }.also { it.start() }
        } else if ((!turbo || !active) && gpu != null) {
            gpu?.stop()
            gpu = null
        }
    }

    /**
     * Duty-cycled busy loop: spin for `duty` of each window, sleep for the rest.
     * Mixes streaming memory traffic (4 MB per thread, past the caches) with
     * floating-point math so both the cores and the memory system draw power.
     */
    private fun burn() {
        // Normal priority so the scheduler moves this onto the big cores.
        Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT)
        val buf = FloatArray(1 shl 20)
        val mask = buf.size - 1
        var idx = 0
        var x = 1.0001
        var sink = 0.0
        val windowNs = WINDOW_MS * 1_000_000L
        while (active) {
            val d = duty
            val start = System.nanoTime()
            val busyNs = (windowNs * d).toLong()
            while (System.nanoTime() - start < busyNs) {
                for (i in 0 until 8192) {
                    val j = (idx + i) and mask
                    val v = buf[j] * 0.9999f + 1.0001f
                    buf[j] = v
                }
                idx = (idx + 8192) and mask
                for (i in 0 until 512) {
                    x = x * 1.0000001 + 0.0000001
                    sink += Math.sqrt(x) * Math.sin(x)
                }
                if (x > 1e6) x = 1.0001
            }
            val restMs = (windowNs - (System.nanoTime() - start)) / 1_000_000L
            if (restMs > 0) {
                try {
                    Thread.sleep(restMs)
                } catch (_: InterruptedException) {
                    break
                }
            }
        }
        // Keep the JIT from discarding the work.
        if (sink == 42.0 || buf[idx] == 42f) println()
    }

    private fun shutdown(reason: String) {
        handler.removeCallbacks(controlLoop)
        active = false
        duty = 0f
        isRunning = false
        status = "Off"
        lastStopReason = reason
        workers.forEach { it.interrupt() }
        workers.clear()
        syncGpu()
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
        RoomTemp.markHeatEnded(this)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        WarmerTileService.refresh(this)
    }

    /** Short double pulse: "warm and ready". */
    private fun buzz() {
        val v = getSystemService(Vibrator::class.java) ?: return
        if (!v.hasVibrator()) return
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 90, 40), -1))
    }

    override fun onDestroy() {
        if (active) shutdown("Stopped")
        super.onDestroy()
    }

    private fun acquireWakeLock(minutes: Int) {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HandWarmer::heat").apply {
            setReferenceCounted(false)
            // Timeout is a backstop in case the service is killed without onDestroy.
            acquire(minutes * 60_000L + 60_000L)
        }
    }

    private fun startInForeground(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun notificationManager(): NotificationManager {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Hand warmer", NotificationManager.IMPORTANCE_LOW)
            )
        }
        return nm
    }

    private fun buildNotification(text: String): Notification {
        notificationManager()
        val openApp = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stop = PendingIntent.getService(
            this, 1, Intent(this, HeaterService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_flame)
            .setContentTitle(if (overdrive) "Hand warmer · OVERDRIVE" else "Hand warmer on")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openApp)
            .addAction(Notification.Action.Builder(null, "Stop", stop).build())
            .build()
    }
}
