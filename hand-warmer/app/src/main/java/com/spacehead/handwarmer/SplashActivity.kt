package com.spacehead.handwarmer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.spacehead.handwarmer.ui.ArcLoaderView
import com.spacehead.handwarmer.ui.edgeToEdge

/**
 * Arcane Labs boot screen: 5.5 s, not skippable.
 * 0–4.6 s the ring fills and the boot log types out; the last 0.9 s holds on the signature.
 */
class SplashActivity : Activity() {

    companion object {
        private const val TOTAL_MS = 5500L
        private const val BOOT_MS = 4600L
    }

    private var animator: ValueAnimator? = null
    private var launched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdge()
        setContentView(R.layout.activity_splash)

        val loader = findViewById<ArcLoaderView>(R.id.loader)
        val brand = findViewById<TextView>(R.id.brand)
        val rule = findViewById<View>(R.id.rule)
        val log = findViewById<TextView>(R.id.bootLog)
        val version = runCatching { packageManager.getPackageInfo(packageName, 0).versionName }.getOrNull() ?: ""

        val bootLog = listOf(
            "> arcane.labs :: ignis v$version",
            "> probing battery thermistor ........ ok",
            "> thermal governor link ............. ok",
            "> gpu turbo pipeline ................ ok",
            "> cores online ...................... ${Runtime.getRuntime().availableProcessors()}",
            "> safety interlocks armed ........... ${HeaterService.NORMAL_LIMIT_C}°C",
            "> ready."
        ).joinToString("\n")

        brand.animate().alpha(1f).setStartDelay(400).setDuration(1200).start()
        rule.animate().scaleX(1f).setStartDelay(900).setDuration(1400)
            .setInterpolator(DecelerateInterpolator()).start()

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = TOTAL_MS
            interpolator = LinearInterpolator()
            addUpdateListener {
                val elapsed = (it.animatedValue as Float) * TOTAL_MS
                val p = (elapsed / BOOT_MS).coerceAtMost(1f)
                // Ease the ring; type the log linearly with a blinking cursor.
                loader.progress = 1f - (1f - p) * (1f - p)
                val chars = (p * bootLog.length).toInt()
                val cursor = if ((elapsed / 300).toInt() % 2 == 0) "_" else " "
                log.text = bootLog.substring(0, chars) + if (p < 1f) cursor else ""
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) = launchMain()
            })
            start()
        }
    }

    @Suppress("DEPRECATION")
    private fun launchMain() {
        if (launched || isFinishing) return
        launched = true
        startActivity(Intent(this, MainActivity::class.java).putExtras(intent))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        animator?.removeAllListeners()
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        super.onDestroy()
    }
}
