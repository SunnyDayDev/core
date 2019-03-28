package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.currentStackTraceElement
import io.reactivex.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 28.08.2018.
 * mail: mail@sunnyday.dev
 */

fun Completable.markErrorStackTrace(stackTraceElement: StackTraceElement = currentStackTraceElement()): Completable {
    val marker = StackTraceMarker.create(stackTraceElement)
    return mapError(marker::mark)
}

fun <T> Maybe<T>.markErrorStackTrace(stackTraceElement: StackTraceElement = currentStackTraceElement()): Maybe<T> {
    val marker = StackTraceMarker.create(stackTraceElement)
    return mapError(marker::mark)
}

fun <T> Single<T>.markErrorStackTrace(stackTraceElement: StackTraceElement = currentStackTraceElement()): Single<T> {
    val marker = StackTraceMarker.create(stackTraceElement)
    return mapError(marker::mark)
}

fun <T> Observable<T>.markErrorStackTrace(stackTraceElement: StackTraceElement = currentStackTraceElement()): Observable<T> {
    val marker = StackTraceMarker.create(stackTraceElement)
    return mapError(marker::mark)
}

fun <T> Flowable<T>.markErrorStackTrace(stackTraceElement: StackTraceElement = currentStackTraceElement()): Flowable<T> {
    val marker = StackTraceMarker.create(stackTraceElement)
    return mapError(marker::mark)
}

class StackTraceMarker private constructor(
        private val markerLine: StackTraceElement
) {

    companion object {

        val SEPARATOR = StackTraceElement(
                "...", "...",
                "End of Rx stacktrace markers", -1
        )

        fun create(line: StackTraceElement): StackTraceMarker {
            return StackTraceMarker(line)
        }

    }

    internal fun mark(e: Throwable): Throwable = e.apply {

        val marker =
                if (stackTrace.find { it === SEPARATOR } != null) arrayOf(markerLine)
                else arrayOf(markerLine, SEPARATOR)

        stackTrace = marker + stackTrace

    }

}