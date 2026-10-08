package com.spacehead.handwarmer.ui

import android.content.res.Resources
import android.graphics.Color

object Palette {
    val BG = Color.parseColor("#07080A")
    val SURFACE = Color.parseColor("#0E1013")
    val LINE = Color.parseColor("#1C2026")
    val GRID = Color.parseColor("#14171C")
    val TEXT = Color.parseColor("#E6E8EB")
    val DIM = Color.parseColor("#5C6470")
    val HEAT = Color.parseColor("#FF6A1A")
    val HEAT_HOT = Color.parseColor("#FF3B30")
    val COOL = Color.parseColor("#3FD0FF")
    val OK = Color.parseColor("#3DDC97")
}

val Number.dp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.density
val Number.sp: Float get() = this.toFloat() * Resources.getSystem().displayMetrics.scaledDensity
