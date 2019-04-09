package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.currentStackTraceElement
import io.reactivex.*
import timber.log.Timber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-20.
 * mail: mail@sunnyday.dev
 */

object RxDebug {

    var enabled = false
    
}

fun <T: Any> Flowable<T>.debug(tag: String? = null,
                               mapResult: (T) -> String = defaultResultMapper): Flowable<T> {

    val resolvedTag = tag ?: tagByStackTrace()

    if (!RxDebug.enabled) return this

    return this
            .doOnSubscribe { Timber.d("RxDebug[$resolvedTag]: onSubscribe") }
            .doOnNext { Timber.d("RxDebug[$resolvedTag]: onNext: ${mapResult(it)}") }
            .doOnError { Timber.d("RxDebug[$resolvedTag]: onError: $it")  }
            .doOnComplete { Timber.d("RxDebug[$resolvedTag]: onComplete") }
            .doFinally { Timber.d("RxDebug[$resolvedTag]: onFinally") }
            .doOnCancel { Timber.d("RxDebug[$resolvedTag]: onCancel") }
            .markErrorStackTrace(currentStackTraceElement(shift = 1))

}

fun <T: Any> Observable<T>.debug(tag: String? = null,
                                 mapResult: (T) -> String = defaultResultMapper): Observable<T> {

    val resolvedTag = tag ?: tagByStackTrace()

    if (!RxDebug.enabled) return this

    return this
            .doOnSubscribe { Timber.d("RxDebug[$resolvedTag]: onSubscribe") }
            .doOnNext { Timber.d("RxDebug[$resolvedTag]: onNext: ${mapResult(it)}") }
            .doOnError { Timber.d("RxDebug[$resolvedTag]: onError: $it")  }
            .doOnComplete { Timber.d("RxDebug[$resolvedTag]: onComplete") }
            .doFinally { Timber.d("RxDebug[$resolvedTag]: onFinally") }
            .doOnDispose { Timber.d("RxDebug[$resolvedTag]: onDispose") }
            .markErrorStackTrace(currentStackTraceElement(shift = 1))

}

fun <T: Any> Single<T>.debug(tag: String? = null,
                             mapResult: (T) -> String = defaultResultMapper): Single<T> {

    val resolvedTag = tag ?: tagByStackTrace()

    if (!RxDebug.enabled) return this

    return this
            .doOnSubscribe { Timber.d("RxDebug[$resolvedTag]: onSubscribe") }
            .doOnSuccess { Timber.d("RxDebug[$resolvedTag]: onNext: ${mapResult(it)}") }
            .doOnError { Timber.d("RxDebug[$resolvedTag]: onError: $it")  }
            .doFinally { Timber.d("RxDebug[$resolvedTag]: onFinally") }
            .doOnDispose { Timber.d("RxDebug[$resolvedTag]: onDispose") }
            .markErrorStackTrace(currentStackTraceElement(shift = 1))

}

fun <T: Any> Maybe<T>.debug(tag: String? = null,
                            mapResult: (T) -> String = defaultResultMapper): Maybe<T> {

    val resolvedTag = tag ?: tagByStackTrace()

    if (!RxDebug.enabled) return this

    return this
            .doOnSubscribe { Timber.d("RxDebug[$resolvedTag]: onSubscribe") }
            .doOnSuccess { Timber.d("RxDebug[$resolvedTag]: onNext: ${mapResult(it)}") }
            .doOnError { Timber.d("RxDebug[$resolvedTag]: onError: $it")  }
            .doOnComplete { Timber.d("RxDebug[$resolvedTag]: onComplete") }
            .doFinally { Timber.d("RxDebug[$resolvedTag]: onFinally") }
            .doOnDispose { Timber.d("RxDebug[$resolvedTag]: onDispose") }
            .markErrorStackTrace(currentStackTraceElement(shift = 1))

}

fun Completable.debug(tag: String? = null): Completable {

    val resolvedTag = tag ?: tagByStackTrace()

    if (!RxDebug.enabled) return this

    return this
            .doOnSubscribe { Timber.d("RxDebug[$resolvedTag]: onSubscribe") }
            .doOnError { Timber.d("RxDebug[$resolvedTag]: onError: $it")  }
            .doOnComplete { Timber.d("RxDebug[$resolvedTag]: onComplete") }
            .doFinally { Timber.d("RxDebug[$resolvedTag]: onFinally") }
            .doOnDispose { Timber.d("RxDebug[$resolvedTag]: onDispose") }
            .markErrorStackTrace(currentStackTraceElement(shift = 1))

}

// region Utils

private fun tagByStackTrace(): String {
    val callLine = Thread.currentThread().stackTrace[5]
    return "${callLine.className.split(".").last()}.${callLine.methodName}:${callLine.lineNumber}"
}

private val defaultResultMapper: (Any) -> String get() = { "$it" }

// endregion