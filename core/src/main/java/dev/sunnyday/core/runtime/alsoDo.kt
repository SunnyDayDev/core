package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

infix fun <T> T.alsoDo(@Suppress("UNUSED_PARAMETER") stub: Any): T = this

inline infix fun <T> T.alsoDo(action: () -> Any): T {
    action()
    return this
}