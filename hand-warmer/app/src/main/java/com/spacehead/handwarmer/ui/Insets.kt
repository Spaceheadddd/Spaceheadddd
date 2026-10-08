package com.spacehead.handwarmer.ui

import android.os.Build
import android.view.View
import android.view.WindowInsets

/**
 * Pads this view by the status/navigation bar insets on top of its own padding.
 * Android 15 draws apps edge-to-edge, so without this content slides under the bars.
 */
fun View.applySystemBarPadding() {
    val l = paddingLeft
    val t = paddingTop
    val r = paddingRight
    val b = paddingBottom
    setOnApplyWindowInsetsListener { v, insets ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bars = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            v.setPadding(l + bars.left, t + bars.top, r + bars.right, b + bars.bottom)
        } else {
            @Suppress("DEPRECATION")
            v.setPadding(
                l + insets.systemWindowInsetLeft, t + insets.systemWindowInsetTop,
                r + insets.systemWindowInsetRight, b + insets.systemWindowInsetBottom
            )
        }
        insets
    }
    requestApplyInsets()
}

/** Draw behind the system bars on every Android version, so insets are always delivered. */
fun android.app.Activity.edgeToEdge() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) window.setDecorFitsSystemWindows(false)
}
