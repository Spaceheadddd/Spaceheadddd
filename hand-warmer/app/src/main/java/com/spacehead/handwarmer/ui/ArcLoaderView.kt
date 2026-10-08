package com.spacehead.handwarmer.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/** Counter-rotating arcs around a progress ring and percentage readout. */
class ArcLoaderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var progress = 0f
        set(v) { field = v.coerceIn(0f, 1f) }

    private val start = SystemClock.uptimeMillis()
    private val ring = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 2.dp; color = Palette.LINE
    }
    private val prog = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 2.dp; color = Palette.HEAT; strokeCap = Paint.Cap.ROUND
    }
    private val spin = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; strokeWidth = 1.dp; color = Palette.DIM; strokeCap = Paint.Cap.ROUND
    }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Palette.TEXT; textSize = 14.sp; textAlign = Paint.Align.CENTER; typeface = Typeface.MONOSPACE
    }
    private val rect = RectF()

    override fun onDraw(canvas: Canvas) {
        val t = (SystemClock.uptimeMillis() - start) / 1000f
        val cx = width / 2f
        val cy = height / 2f
        val r = min(width, height) / 2f - 4.dp

        rect.set(cx - r, cy - r, cx + r, cy + r)
        canvas.drawArc(rect, 0f, 360f, false, ring)
        canvas.drawArc(rect, -90f, 360f * progress, false, prog)

        val r2 = r - 12.dp
        rect.set(cx - r2, cy - r2, cx + r2, cy + r2)
        canvas.drawArc(rect, t * 220f, 70f, false, spin)
        canvas.drawArc(rect, t * 220f + 180f, 70f, false, spin)

        val r3 = r - 22.dp
        rect.set(cx - r3, cy - r3, cx + r3, cy + r3)
        spin.color = Palette.HEAT
        canvas.drawArc(rect, -t * 320f, 40f, false, spin)
        spin.color = Palette.DIM

        canvas.drawText("%03d".format((progress * 100).toInt()), cx, cy + 5.dp, text)
        postInvalidateOnAnimation()
    }
}
