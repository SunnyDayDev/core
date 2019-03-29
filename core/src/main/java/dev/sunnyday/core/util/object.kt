package dev.sunnyday.core.util

import dev.sunnyday.core.runtime.tryOptional

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

@Suppress("UNCHECKED_CAST")
inline fun <T> equals(current: T, other: Any?, check: T.(T) -> Boolean): Boolean {
    val typedOther = tryOptional { other as? T } ?: return false
    return check(current, typedOther)
}