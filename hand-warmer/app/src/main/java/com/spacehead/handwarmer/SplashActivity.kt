package com.spacehead.handwarmer

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import com.spacehead.handwarmer.ui.ArcLoaderView

/** Arcane Labs boot screen: loader ring, signature, and a short boot log. Tap to skip. */
class SplashActivity : Activity() {

    private val bootLines = listOf(
        "> arcane.labs :: thermal core v1.0",
        "> probing battery thermistor ........ ok",
        "> thermal governor link ............. ok",
        "> cores online ...................... %d",
        "> safety interlocks armed ........... 44.0°C",
        "> ready."
    )

    private var animator: ValueAnimator? = null
    private var launched = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val loader = findViewById<ArcLoaderView>(R.id.loader)
        val brand = findViewById<TextView>(R.id.brand)
        val rule = findViewById<View>(R.id.rule)
        val log = findViewById<TextView>(R.id.bootLog)
        val cores = Runtime.getRuntime().availableProcessors()
        val lines = bootLines.map { if (it.contains("%d")) it.format(cores) else it }

        brand.animate().alpha(1f).setStartDelay(250).setDuration(700).start()
        rule.animate().scaleX(1f).setStartDelay(450).setDuration(800)
            .setInterpolator(DecelerateInterpolator()).start()

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 2200
            interpolator = DecelerateInterpolator(1.4f)
            addUpdateListener {
                val p = it.animatedValue as Float
                loader.progress = p
                val shown = (p * lines.size + 0.3f).toInt().coerceAtMost(lines.size)
                log.text = lines.take(shown).joinToString("\n")
            }
            doOnEnd { launchMain() }
            start()
        }

        findViewById<View>(R.id.root).setOnClickListener { launchMain() }
    }

    private fun ValueAnimator.doOnEnd(block: () -> Unit) {
        addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) = block()
        })
    }

    @Suppress("DEPRECATION")
    private fun launchMain() {
        if (launched) return
        launched = true
        animator?.cancel()
        startActivity(Intent(this, MainActivity::class.java).putExtras(intent))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onDestroy() {
        animator?.removeAllUpdateListeners()
        animator?.cancel()
        super.onDestroy()
    }
}
