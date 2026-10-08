package com.spacehead.handwarmer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** 270° ring gauge: temperature arc, target marker, inner power (duty) arc. */
class ThermalGaugeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var tempC: Float? = null
        set(v) { field = v; invalidate() }
    var targetC: Float = 38f
        set(v) { field = v; invalidate() }
    var duty: Float = 0f
        set(v) { field = v; invalidate() }
    var active: Boolean = false
        set(v) { field = v; invalidate() }
    /** Safety cutoff, drawn as a red tick on the scale. */
    var limitC: Float = 46f
        set(v) { field = v; invalidate() }
    var overdrive: Boolean = false
        set(v) { field = v; invalidate() }

    private val minC = 20f
    private val maxC = 50f
    private val startAngle = 135f
    private val sweep = 270f

    private val track = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 10.dp; color = Palette.LINE; strokeCap = Paint.Cap.ROUND
    }
    private val value = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 10.dp; strokeCap = Paint.Cap.ROUND
    }
    private val dutyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 3.dp; strokeCap = Paint.Cap.ROUND
    }
    private val tick = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 1.dp; color = Palette.DIM }
    private val marker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 3.dp; color = Palette.TEXT; strokeCap = Paint.Cap.ROUND
    }
    private val big = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Palette.TEXT; textSize = 54.sp; textAlign = Paint.Align.CENTER
        typeface = Typeface.create("monospace", Typeface.NORMAL)
    }
    private val small = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Palette.DIM; textSize = 11.sp; textAlign = Paint.Align.CENTER
        typeface = Typeface.MONOSPACE; letterSpacing = 0.2f
    }

    private val oval = RectF()
    private val inner = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val size = min(w, 300.dp.toInt())
        setMeasuredDimension(w, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val r = min(w, h) / 2f - 14.dp
        val cx = w / 2f
        val cy = h / 2f + 8.dp
        oval.set(cx - r, cy - r, cx + r, cy + r)
        val ir = r - 20.dp
        inner.set(cx - ir, cy - ir, cx + ir, cy + ir)
        val grad = SweepGradient(
            cx, cy,
            // The arc covers gradient positions 0..0.75 once rotated to start at the arc start.
            intArrayOf(Palette.COOL, Palette.COOL, Palette.HEAT, Palette.HEAT_HOT, Palette.HEAT_HOT, Palette.COOL),
            floatArrayOf(0f, 0.25f, 0.5f, 0.75f, 0.95f, 1f)
        )
        val m = android.graphics.Matrix().apply { setRotate(startAngle, cx, cy) }
        grad.setLocalMatrix(m)
        value.shader = grad
    }

    private fun angleFor(c: Float) = startAngle + sweep * ((c - minC) / (maxC - minC)).coerceIn(0f, 1f)

    override fun onDraw(canvas: Canvas) {
        val cx = oval.centerX()
        val cy = oval.centerY()
        val r = oval.width() / 2f

        // Ticks every 1 °C, longer every 5 °C.
        for (c in minC.toInt()..maxC.toInt()) {
            val a = Math.toRadians(angleFor(c.toFloat()).toDouble())
            val major = c % 5 == 0
            val r1 = r + 9.dp
            val r2 = r1 + if (major) 6.dp else 3.dp
            tick.color = when {
                c >= limitC -> Palette.HEAT_HOT
                major -> Palette.DIM
                else -> Palette.LINE
            }
            canvas.drawLine(
                cx + (r1 * cos(a)).toFloat(), cy + (r1 * sin(a)).toFloat(),
                cx + (r2 * cos(a)).toFloat(), cy + (r2 * sin(a)).toFloat(), tick
            )
        }

        canvas.drawArc(oval, startAngle, sweep, false, track)
        tempC?.let { canvas.drawArc(oval, startAngle, angleFor(it) - startAngle, false, value) }

        // Inner power arc.
        dutyPaint.color = Palette.LINE
        canvas.drawArc(inner, startAngle, sweep, false, dutyPaint)
        if (active && duty > 0.01f) {
            dutyPaint.color = Palette.HEAT
            canvas.drawArc(inner, startAngle, sweep * duty, false, dutyPaint)
        }

        // Target marker crossing the main ring.
        marker.color = if (overdrive) Palette.HEAT_HOT else Palette.TEXT
        val ta = Math.toRadians(angleFor(targetC).toDouble())
        val m1 = r - 10.dp
        val m2 = r + 10.dp
        canvas.drawLine(
            cx + (m1 * cos(ta)).toFloat(), cy + (m1 * sin(ta)).toFloat(),
            cx + (m2 * cos(ta)).toFloat(), cy + (m2 * sin(ta)).toFloat(), marker
        )

        val t = tempC
        canvas.drawText(if (t != null) "%.1f".format(t) else "--.-", cx, cy + 14.dp, big)
        small.color = Palette.DIM
        canvas.drawText("BATTERY °C", cx, cy - 34.dp, small)
        small.color = when {
            overdrive -> Palette.HEAT_HOT
            active -> Palette.HEAT
            else -> Palette.DIM
        }
        canvas.drawText("TARGET %.0f°  ·  PWR %d%%".format(targetC, if (active) (duty * 100).toInt() else 0), cx, cy + 40.dp, small)
    }
}
