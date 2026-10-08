package com.spacehead.handwarmer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import com.spacehead.handwarmer.Monitor
import kotlin.math.ceil
import kotlin.math.floor

/** Rolling temperature trace with target line and power (duty) histogram underneath. */
class HeatGraphView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var samples: List<Monitor.Sample> = emptyList()
        set(v) { field = v; invalidate() }
    var targetC: Float = 38f
        set(v) { field = v; invalidate() }

    private val grid = Paint().apply { color = Palette.GRID; strokeWidth = 1.dp }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 2.dp; color = Palette.HEAT
        strokeJoin = Paint.Join.ROUND
    }
    private val target = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1.dp; color = Palette.DIM
        pathEffect = DashPathEffect(floatArrayOf(6.dp, 6.dp), 0f)
    }
    private val bar = Paint().apply { color = Palette.HEAT; alpha = 50 }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Palette.DIM; textSize = 10.sp; typeface = Typeface.MONOSPACE
    }
    private val path = Path()

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        val left = 30.dp
        val top = 6.dp
        val bottom = h - 6.dp

        val pts = samples
        var lo = targetC - 3f
        var hi = targetC + 3f
        for (s in pts) { if (s.tempC < lo) lo = s.tempC; if (s.tempC > hi) hi = s.tempC }
        lo = floor(lo - 0.5f)
        hi = ceil(hi + 0.5f)

        fun y(c: Float) = bottom - (c - lo) / (hi - lo) * (bottom - top)

        // Horizontal grid every 2 °C.
        var g = ceil(lo / 2f) * 2f
        while (g <= hi) {
            val gy = y(g)
            canvas.drawLine(left, gy, w, gy, grid)
            canvas.drawText("%.0f".format(g), 0f, gy + 4.dp, label)
            g += 2f
        }

        val ty = y(targetC)
        canvas.drawLine(left, ty, w, ty, target)

        if (pts.size < 2) {
            label.textAlign = Paint.Align.CENTER
            canvas.drawText("AWAITING DATA", (left + w) / 2f, h / 2f, label)
            label.textAlign = Paint.Align.LEFT
            return
        }

        val now = SystemClock.elapsedRealtime()
        fun x(t: Long) = left + (1f - (now - t).toFloat() / Monitor.WINDOW_MS) * (w - left)

        // Power bars along the bottom quarter.
        val barH = (bottom - top) * 0.25f
        for (i in 1 until pts.size) {
            val d = pts[i].duty
            if (d <= 0.01f) continue
            canvas.drawRect(x(pts[i - 1].atElapsed), bottom - barH * d, x(pts[i].atElapsed), bottom, bar)
        }

        path.reset()
        pts.forEachIndexed { i, s ->
            if (i == 0) path.moveTo(x(s.atElapsed), y(s.tempC)) else path.lineTo(x(s.atElapsed), y(s.tempC))
        }
        canvas.drawPath(path, line)
    }
}
