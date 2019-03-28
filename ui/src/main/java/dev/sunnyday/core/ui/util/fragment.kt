package dev.sunnyday.core.ui.util

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

inline fun <T: Fragment> T.withArguments(build: Bundle.() -> Unit): T {
    val bundle = arguments ?: Bundle()
    arguments = bundle.apply(build)
    return this
}