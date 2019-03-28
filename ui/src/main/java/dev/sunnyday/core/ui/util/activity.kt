package dev.sunnyday.core.ui.util

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

fun Activity.findViewWithTag(id: Int, value: Any) : View? {

    val contentView = contentView ?: return null

    return when {
        contentView is ViewGroup -> contentView.findViewWithTag(id, value)
        contentView.getTag(id) == value -> contentView
        else -> null
    }

}

fun Activity.findViewWithTransitionName(value: String) : View? {

    val contentView = contentView ?: return null

    return when {
        contentView is ViewGroup -> contentView.findViewWithTransitionName(value)
        ViewCompat.getTransitionName(contentView) == value -> contentView
        else -> null
    }

}

val Activity.contentView: View? get() = window.decorView
    .findViewById<ViewGroup>(android.R.id.content).getChildAt(0)

fun View.findActivity(): Activity? {

    var context = context

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }

    return null

}