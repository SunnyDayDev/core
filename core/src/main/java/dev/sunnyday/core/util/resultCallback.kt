package dev.sunnyday.core.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-05.
 * mail: mail@sunnydaydev.me
 */

typealias ResultCallback<T> = (Result<T>) -> Unit

fun ResultCallback<Unit>.success() {
    this(Result.success(Unit))
}

fun <T> ResultCallback<T>.success(value: T) {
    this(Result.success(value))
}

fun <T> ResultCallback<T>.failure(e: Throwable) {
    this(Result.failure(e))
}