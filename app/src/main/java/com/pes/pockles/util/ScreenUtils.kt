package com.pes.pockles.util

import android.content.Context
import android.graphics.Point
import android.view.WindowManager

fun getDisplaySize(context: Context): IntArray {
    val result = IntArray(2)
    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getRealSize(size)
    result[0] = size.y
    result[1] = size.x
    return result
}

fun dp2px(c: Context, dp: Float): Float {
    val density: Float = c.resources.displayMetrics.density
    return dp * density
}

fun px2dp(c: Context, pixel: Float): Float {
    val density: Float = c.resources.displayMetrics.density
    return pixel / density
}