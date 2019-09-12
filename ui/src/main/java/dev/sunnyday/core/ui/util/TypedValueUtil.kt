package dev.sunnyday.core.ui.util

import android.util.DisplayMetrics
import android.util.TypedValue


object TypedValueUtil {

    fun dip(value: Int, displayMetrics: DisplayMetrics): Int {

        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            displayMetrics
        ).toInt()

    }

}