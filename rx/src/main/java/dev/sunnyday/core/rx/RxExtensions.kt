package dev.sunnyday.core.rx

import io.reactivex.*
import io.reactivex.rxkotlin.Singles
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by sunny on 25.05.2018.
 * mail: mail@sunnyday.dev
 */

fun <T> Observable<T>.doOnError(action: () -> Unit): Observable<T> =
        doOnError { action() }

fun <T> Observable<T>.firstOrError(selector: (T) -> Boolean): Single<T> =
        filter(selector).firstOrError()

fun <T> Completable.andThen(value: T): Single<T> = andThen(Single.just(value))

// region: retry

fun <T> Observable<T>.retryWithDelay(delay: Long, timeUnit: TimeUnit): Observable<T> =
        retryWhen { errors -> errors.flatMap { Observable.timer(delay, timeUnit) } }

fun <T> Flowable<T>.retryWhen(trigger: Observable<Unit>): Flowable<T> =
        retryWhen { errors -> errors.flatMap { trigger.toFlowable(BackpressureStrategy.LATEST) } }

fun <T> Observable<T>.retryWhen(trigger: Observable<Unit>): Observable<T> =
        retryWhen { errors -> errors.flatMap { trigger } }

fun Completable.retryWhen(trigger: Observable<Unit>): Completable =
        retryWhen { errors -> errors.flatMap { trigger.toFlowable(BackpressureStrategy.LATEST) } }


fun <T> Observable<T>.retryWithAction(action: (Throwable) -> Unit): Observable<T> =
        retry { e: Throwable -> action(e); true }

// endregion

// region: finalizers

fun <T> Observable<T>.doFinally(finalizers: List<() -> Unit>): Observable<T> =
        doFinally { finalizers.forEach { it() } }

fun Completable.doFinally(finalizers: List<() -> Unit>): Completable =
        doFinally { finalizers.forEach { it() } }

// endregion
