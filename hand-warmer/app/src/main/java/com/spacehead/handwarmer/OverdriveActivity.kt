package com.spacehead.handwarmer

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.CheckBox
import android.widget.TextView
import com.spacehead.handwarmer.ui.applySystemBarPadding
import com.spacehead.handwarmer.ui.edgeToEdge

/**
 * Explains Overdrive and asks for explicit consent. The enable button unlocks only after
 * the box is ticked and, the first time, after a short reading countdown.
 * Returns RESULT_OK when the user enables Overdrive.
 */
class OverdriveActivity : Activity() {

    private var timer: CountDownTimer? = null
    private var countdownDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdge()
        setContentView(R.layout.activity_overdrive)
        findViewById<android.view.View>(R.id.content).applySystemBarPadding()

        val ack = findViewById<CheckBox>(R.id.ack)
        val enable = findViewById<TextView>(R.id.enable)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val seenBefore = prefs.getBoolean("overdriveSeen", false)

        fun update() {
            enable.isEnabled = ack.isChecked && countdownDone
            enable.setTextColor(getColor(if (enable.isEnabled) R.color.bg else R.color.dim))
        }

        if (seenBefore) {
            countdownDone = true
        } else {
            timer = object : CountDownTimer(10_000, 250) {
                override fun onTick(msLeft: Long) {
                    enable.text = "READ FIRST · %d".format((msLeft + 999) / 1000)
                }
                override fun onFinish() {
                    countdownDone = true
                    enable.text = "ENABLE OVERDRIVE"
                    update()
                }
            }.start()
        }

        ack.setOnCheckedChangeListener { _, _ -> update() }
        enable.setOnClickListener {
            prefs.edit().putBoolean("overdriveSeen", true).apply()
            setResult(RESULT_OK)
            finish()
        }
        findViewById<TextView>(R.id.cancel).setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
        update()
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}
