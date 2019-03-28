package dev.sunnyday.core.ui.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.forEach

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

fun ViewGroup.findView(check: (View) -> Boolean) : View? {

    if (check(this)) return this

    forEach {
        when {
            it is ViewGroup -> return it.findView(check) ?: return@forEach
            check(it) -> return it
        }
    }

    return null

}

fun ViewGroup.findViewWithTag(id: Int, value: Any) : View? = findView {
    it.getTag(id) == value
}

fun ViewGroup.findViewWithTransitionName(value: String) : View? = findView {
    ViewCompat.getTransitionName(this) == value
}