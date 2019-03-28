package dev.sunnyday.core.runtime

import timber.log.Timber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

inline fun <T> tryOptional(logError: Boolean = false, action: () -> T): T? {
    return try {
        action()
    } catch (e: Throwable) {
        if (logError) Timber.e(e)
        null
    }
}