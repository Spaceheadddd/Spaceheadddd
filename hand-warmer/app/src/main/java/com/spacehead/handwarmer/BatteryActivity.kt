package com.spacehead.handwarmer

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.spacehead.handwarmer.ui.Palette
import com.spacehead.handwarmer.ui.applySystemBarPadding
import com.spacehead.handwarmer.ui.dp
import com.spacehead.handwarmer.ui.edgeToEdge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Full battery drain breakdown: live drain, normal baselines, per-mode comparison, history. */
class BatteryActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private val ticker = object : Runnable {
        override fun run() {
            refreshLive()
            handler.postDelayed(this, 2000)
        }
    }

    private lateinit var content: LinearLayout
    private lateinit var liveBig: TextView
    private lateinit var liveSub: TextView
    private lateinit var powerVal: TextView
    private lateinit var normalVal: TextView
    private lateinit var ignisVal: TextView
    private lateinit var emptyVal: TextView
    private lateinit var capacityVal: TextView
    private lateinit var levelVal: TextView
    private lateinit var sessionCard: LinearLayout
    private lateinit var sessionDur: TextView
    private lateinit var sessionUsed: TextView
    private lateinit var sessionShare: TextView
    private lateinit var historyHost: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        edgeToEdge()
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20.dp.toInt(), 16.dp.toInt(), 20.dp.toInt(), 28.dp.toInt())
        }
        setContentView(ScrollView(this).apply {
            setBackgroundColor(Palette.BG)
            addView(content)
        })
        content.applySystemBarPadding()

        // Header
        content.addView(LinearLayout(this).apply {
            gravity = Gravity.CENTER_VERTICAL
            addView(text("‹  IGNIS", 12f, Palette.DIM, 0.3f).apply { setOnClickListener { finish() } })
            addView(View(this@BatteryActivity), LinearLayout.LayoutParams(0, 1, 1f))
            addView(text("BATTERY", 13f, Palette.TEXT, 0.35f))
        })

        // Live drain
        val live = card(top = 20)
        live.addView(label("DRAIN NOW"))
        liveBig = text("--", 44f, Palette.TEXT).also { live.addView(it) }
        liveSub = text("", 11f, Palette.DIM).also { live.addView(it) }
        val r1 = row(live, 14)
        powerVal = cell(r1, "POWER")
        normalVal = cell(r1, "NORMAL")
        ignisVal = cell(r1, "IGNIS")
        val r2 = row(live, 14)
        emptyVal = cell(r2, "TO EMPTY")
        levelVal = cell(r2, "LEVEL")
        capacityVal = cell(r2, "CAPACITY")

        // Current session
        sessionCard = card(top = 12)
        sessionCard.addView(label("THIS SESSION"))
        val r3 = row(sessionCard, 10)
        sessionDur = cell(r3, "TIME")
        sessionUsed = cell(r3, "USED")
        sessionShare = cell(r3, "IGNIS SHARE")

        // Mode comparison + history are rebuilt in onResume.
        historyHost = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        content.addView(historyHost)

        content.addView(text(
            "How it's measured: total drain comes from live battery current × voltage. Ignis's share is " +
                "total minus your normal drain in the same screen state, learned automatically (screen on: " +
                "while this app is open and idle; screen off: by the background job). Sessions are stored per " +
                "mode; a mode change mid-session starts a new record. Time on the charger isn't counted.",
            10f, Palette.DIM
        ).apply { setPadding(0, 18.dp.toInt(), 0, 0) })

        content.addView(text("CLEAR HISTORY", 10f, Palette.DIM, 0.3f).apply {
            gravity = Gravity.CENTER
            setPadding(0, 24.dp.toInt(), 0, 8.dp.toInt())
            setOnClickListener {
                AlertDialog.Builder(this@BatteryActivity, android.R.style.Theme_Material_Dialog_Alert)
                    .setTitle("Clear drain history?")
                    .setMessage("Removes all recorded sessions. Normal-drain baselines are kept.")
                    .setPositiveButton("Clear") { _, _ ->
                        DrainTracker.clearHistory(this@BatteryActivity)
                        buildHistory()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        buildHistory()
        handler.post(ticker)
    }

    override fun onPause() {
        handler.removeCallbacks(ticker)
        super.onPause()
    }

    private fun pctH(w: Float) = DrainTracker.percentPerHour(this, w)

    private fun refreshLive() {
        val battery = Monitor.readBattery(this)
        val powerW = Monitor.powerWatts(this, battery)
        val running = HeaterService.isRunning
        val base = DrainTracker.baselineW(this, screenOn = true)

        if (battery?.plugged == true) {
            liveBig.text = "CHARGING"
            liveSub.text = "Drain isn't measured on the charger"
        } else if (powerW != null) {
            liveBig.text = "%.1f%%/h".format(pctH(powerW))
            liveSub.text = when {
                base == null -> "Learning your normal drain…"
                else -> "%.1f× your normal screen-on drain".format(powerW / base)
            }
        } else {
            liveBig.text = "--"
            liveSub.text = "This phone doesn't report battery current"
        }

        powerVal.text = powerW?.let { "%.2fW".format(it) } ?: "--"
        normalVal.text = base?.let { "%.1f%%/h".format(pctH(it)) } ?: "LEARNING"
        ignisVal.text = when {
            !running -> "OFF"
            powerW == null || base == null -> "LEARNING"
            else -> "%.1f%%/h".format(pctH((powerW - base).coerceAtLeast(0f)))
        }
        emptyVal.text = if (battery != null && powerW != null && powerW > 0f && !battery.plugged) {
            val hours = battery.percent / pctH(powerW)
            "%dh %02dm".format(hours.toInt(), ((hours % 1f) * 60).toInt())
        } else "--"
        levelVal.text = battery?.let { "${it.percent}%" } ?: "--"
        capacityVal.text = "%d mAh".format(DrainTracker.capacityMah(this))

        sessionCard.visibility = if (running) View.VISIBLE else View.GONE
        if (running) {
            val sec = DrainTracker.liveSeconds.toInt()
            sessionDur.text = "%d:%02d".format(sec / 60, sec % 60)
            sessionUsed.text = "%.1f%%".format(DrainTracker.livePercentUsed(this))
            sessionShare.text = if (base == null) "LEARNING" else "%.0f%%".format(DrainTracker.liveIgnisShare() * 100)
        }
    }

    /** Mode comparison bars against normal use, then recent sessions. */
    private fun buildHistory() {
        historyHost.removeAllViews()
        val baseOn = DrainTracker.baselineW(this, screenOn = true)
        val baseOff = DrainTracker.baselineW(this, screenOn = false)
        val modes = DrainTracker.modeStats(this)

        val cmp = card(top = 12, parent = historyHost)
        cmp.addView(label("MODE COMPARISON  ·  %/HOUR"))
        val entries = buildList {
            baseOff?.let { add(Triple("Normal · screen off", pctH(it), Palette.DIM)) }
            baseOn?.let { add(Triple("Normal · screen on", pctH(it), Palette.DIM)) }
            modes.forEach {
                add(Triple("${it.mode}  (${it.sessions}×)", it.percentPerHour,
                    if ("OVERDRIVE" in it.mode) Palette.HEAT_HOT else Palette.HEAT))
            }
        }
        if (modes.isEmpty()) {
            cmp.addView(text("No sessions yet. Warm for 2+ minutes in a mode to measure it.", 11f, Palette.DIM)
                .apply { setPadding(0, 10.dp.toInt(), 0, 0) })
        }
        val maxVal = entries.maxOfOrNull { it.second }?.coerceAtLeast(1f) ?: 1f
        entries.forEach { (name, v, color) ->
            val vsNormal = baseOn?.let { b -> if (color != Palette.DIM) "  %.1f×".format(v / pctH(b)) else "" } ?: ""
            cmp.addView(LinearLayout(this).apply {
                setPadding(0, 10.dp.toInt(), 0, 0)
                addView(text(name, 11f, Palette.TEXT), LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                addView(text("%.1f%%/h$vsNormal".format(v), 11f, color))
            })
            cmp.addView(bar(v / maxVal, color))
        }
        if (modes.isNotEmpty()) {
            cmp.addView(text(
                "IGNIS SHARE · " + modes.joinToString("  ·  ") { "${it.mode}: %.0f%%".format(it.ignisShare * 100) },
                10f, Palette.DIM
            ).apply { setPadding(0, 12.dp.toInt(), 0, 0) })
        }

        val sessions = DrainTracker.sessions(this).takeLast(10).reversed()
        if (sessions.isNotEmpty()) {
            val hist = card(top = 12, parent = historyHost)
            hist.addView(label("RECENT SESSIONS"))
            val fmt = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
            sessions.forEach { s ->
                hist.addView(LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 10.dp.toInt(), 0, 0)
                    addView(LinearLayout(this@BatteryActivity).apply {
                        addView(text(s.mode, 12f, Palette.TEXT), LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                        addView(text("%.1f%%".format(s.percentUsed), 12f, Palette.HEAT))
                    })
                    addView(text(
                        "${fmt.format(Date(s.startedAt))} · ${s.durationSec / 60} min · %.1f%%/h · peak %.1f° · Ignis %.0f%%"
                            .format(s.percentPerHour, s.peakC, if (s.avgW > 0f) s.ignisW / s.avgW * 100 else 0f) +
                            (s.mahUsed?.let { " · $it mAh" } ?: ""),
                        10f, Palette.DIM
                    ))
                })
            }
        }
    }

    // ---- View helpers ---------------------------------------------------------------

    private fun text(s: String, size: Float, color: Int, spacing: Float = 0f) = TextView(this).apply {
        text = s
        textSize = size
        letterSpacing = spacing
        setTextColor(color)
    }

    private fun label(s: String) = text(s, 10f, Palette.DIM, 0.25f)

    private fun card(top: Int, parent: LinearLayout = content) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setBackgroundResource(R.drawable.card)
        setPadding(14.dp.toInt(), 14.dp.toInt(), 14.dp.toInt(), 14.dp.toInt())
        parent.addView(this, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            .apply { topMargin = top.dp.toInt() })
    }

    private fun row(parent: LinearLayout, top: Int) = LinearLayout(this).apply {
        parent.addView(this, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            .apply { topMargin = top.dp.toInt() })
    }

    private fun cell(row: LinearLayout, name: String): TextView {
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        box.addView(text(name, 9f, Palette.DIM, 0.25f))
        val v = text("--", 16f, Palette.TEXT).apply { setPadding(0, 4.dp.toInt(), 0, 0) }
        box.addView(v)
        row.addView(box, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        return v
    }

    private fun bar(fraction: Float, color: Int) = LinearLayout(this).apply {
        setBackgroundColor(Palette.LINE)
        val fill = View(this@BatteryActivity).apply { setBackgroundColor(color) }
        val f = fraction.coerceIn(0.02f, 1f)
        addView(fill, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, f))
        addView(View(this@BatteryActivity), LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f - f))
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 4.dp.toInt())
            .apply { topMargin = 4.dp.toInt() }
    }
}
