package dev.sunnyday.core.ui.util

import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue

object Dimen {

    fun dp(value: Number, displayMetrics: DisplayMetrics): Float =
        toPixel(value, TypedValue.COMPLEX_UNIT_DIP, displayMetrics)

    @Suppress("NOTHING_TO_INLINE")
    inline fun dp(value: Number, context: Context): Float =
        dp(value.toFloat(), context.resources.displayMetrics)

    fun sp(value: Number, displayMetrics: DisplayMetrics): Float =
        toPixel(value, TypedValue.COMPLEX_UNIT_SP, displayMetrics)

    @Suppress("NOTHING_TO_INLINE")
    inline fun sp(value: Number, context: Context): Float =
        sp(value, context.resources.displayMetrics)

    @Suppress("NOTHING_TO_INLINE")
    private inline fun toPixel(value: Number, unit: Int, displayMetrics: DisplayMetrics): Float =
        TypedValue.applyDimension(unit, value.toFloat(), displayMetrics)

}