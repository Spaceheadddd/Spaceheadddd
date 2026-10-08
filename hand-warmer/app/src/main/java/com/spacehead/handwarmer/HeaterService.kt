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
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Foreground service that warms the phone by keeping CPU cores busy.
 *
 * Heat output is regulated like a thermostat: worker threads run a duty cycle
 * (busy for part of every 100 ms window, sleep for the rest) and a control loop
 * adjusts that duty from the battery temperature so the phone settles at the
 * chosen target and stays there instead of getting hotter and hotter.
 */
class HeaterService : Service() {

    companion object {
        const val ACTION_START = "com.spacehead.handwarmer.START"
        const val ACTION_STOP = "com.spacehead.handwarmer.STOP"
        const val EXTRA_TARGET_C = "target_c"
        const val EXTRA_MINUTES = "minutes"

        /** Battery temperature at which heating stops no matter what. */
        const val HARD_LIMIT_C = 44.0f
        /** Battery level below which heating stops (when not charging). */
        const val MIN_BATTERY_PCT = 15

        private const val CHANNEL_ID = "heater"
        private const val NOTIFICATION_ID = 1
        private const val CONTROL_INTERVAL_MS = 2000L
        private const val WINDOW_MS = 100L

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
        @Volatile var targetC = 38f
            private set
    }

    private val handler = Handler(Looper.getMainLooper())
    private val workers = mutableListOf<Thread>()
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

        val newTarget = intent?.getFloatExtra(EXTRA_TARGET_C, 38f) ?: 38f
        if (newTarget > targetC) reachedTarget = false
        targetC = newTarget
        val minutes = intent?.getIntExtra(EXTRA_MINUTES, 30) ?: 30
        endsAtElapsed = SystemClock.elapsedRealtime() + minutes * 60_000L

        startInForeground(buildNotification("Starting…"))

        if (!active) {
            active = true
            isRunning = true
            lastStopReason = null
            reachedTarget = false
            acquireWakeLock(minutes)
            startWorkers()
            handler.post(controlLoop)
        } else {
            // Already running: settings were updated, re-arm the wake lock for the new duration.
            wakeLock?.let { if (it.isHeld) it.release() }
            acquireWakeLock(minutes)
            regulate()
        }
        return START_NOT_STICKY
    }

    private fun regulate() {
        val battery = Monitor.readBattery(this)
        if (battery == null) {
            shutdown("Stopped: can't read battery temperature")
            return
        }
        val temp = battery.tempC
        val thermal = (getSystemService(POWER_SERVICE) as PowerManager).currentThermalStatus

        when {
            temp >= HARD_LIMIT_C ->
                return shutdown("Stopped: phone reached %.1f °C".format(temp))
            thermal >= PowerManager.THERMAL_STATUS_SEVERE ->
                return shutdown("Stopped: Android reported thermal stress")
            !battery.plugged && battery.percent < MIN_BATTERY_PCT ->
                return shutdown("Stopped: battery below $MIN_BATTERY_PCT%")
            SystemClock.elapsedRealtime() >= endsAtElapsed ->
                return shutdown("Timer finished")
        }

        if (battery.plugged) {
            // Heat + charging is the worst combination for battery health.
            duty = 0f
            status = "Paused while charging"
        } else {
            // Proportional control: full power when well below target, tapering to
            // zero just above it. A little residual duty near target holds the temp.
            var d = ((targetC + 0.5f - temp) / 2.5f).coerceIn(0f, 1f)
            if (thermal >= PowerManager.THERMAL_STATUS_MODERATE) d = d.coerceAtMost(0.5f)
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

        Monitor.record(temp, duty)
        WarmerTileService.refresh(this)

        val minsLeft = ((endsAtElapsed - SystemClock.elapsedRealtime()) / 60_000L) + 1
        notificationManager().notify(
            NOTIFICATION_ID,
            buildNotification("%.1f °C → %.0f °C · %s · %d min left".format(temp, targetC, status, minsLeft))
        )
    }

    private fun startWorkers() {
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        repeat(cores) { idx ->
            val t = Thread({ burn() }, "heater-$idx")
            t.priority = Thread.MIN_PRIORITY // stay out of the way of the UI and other apps
            t.isDaemon = true
            workers += t
            t.start()
        }
    }

    /** Duty-cycled busy loop: spin for `duty` of each window, sleep for the rest. */
    private fun burn() {
        var x = 1.0001
        var sink = 0.0
        val windowNs = WINDOW_MS * 1_000_000L
        while (active) {
            val d = duty
            val start = System.nanoTime()
            val busyNs = (windowNs * d).toLong()
            while (System.nanoTime() - start < busyNs) {
                for (i in 0 until 2000) {
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
        if (sink == 42.0) println() // keep the JIT from discarding the work
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
        wakeLock?.let { if (it.isHeld) it.release() }
        wakeLock = null
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
            .setContentTitle("Hand warmer on")
            .setContentText(text)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openApp)
            .addAction(Notification.Action.Builder(null, "Stop", stop).build())
            .build()
    }
}
