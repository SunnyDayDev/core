package dev.sunnyday.core.rx

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import org.reactivestreams.Subscriber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-05-21.
 * mail: mail@sunnydaydev.me
 */

interface AppSchedulers {

    val background: Scheduler
    val ui: Scheduler
    val io: Scheduler

    open class Default: AppSchedulers {

        override val background: Scheduler = Schedulers.computation()

        override val ui: Scheduler = AndroidSchedulers.mainThread()

        override val io: Scheduler = Schedulers.io()

    }

}

fun ioToBackground(schedulers: AppSchedulers): SchedulesApplier =
    SchedulesApplier(subscribe = schedulers.io, observe = schedulers.background)

fun ioToUI(schedulers: AppSchedulers): SchedulesApplier =
    SchedulesApplier(subscribe = schedulers.io, observe = schedulers.ui)

fun backgroundToUI(schedulers: AppSchedulers): SchedulesApplier =
    SchedulesApplier(subscribe = schedulers.background, observe = schedulers.ui)

fun ui(schedulers: AppSchedulers): SchedulesApplier =
    SchedulesApplier(subscribe = null, observe = schedulers.ui)

data class SchedulesApplier(
    val subscribe: Scheduler?,
    val observe: Scheduler?
) {

    fun <T> apply(upstream: Flowable<T>): Flowable<T> {
        val subscribed = subscribe?.let(upstream::subscribeOn) ?: upstream
        return observe?.let(subscribed::observeOn) ?: subscribed
    }

    fun <T> apply(upstream: Observable<T>): Observable<T> {
        val subscribed = subscribe?.let(upstream::subscribeOn) ?: upstream
        return observe?.let(subscribed::observeOn) ?: subscribed
    }

    fun <T> apply(upstream: Single<T>): Single<T> {
        val subscribed = subscribe?.let(upstream::subscribeOn) ?: upstream
        return observe?.let(subscribed::observeOn) ?: subscribed
    }

    fun <T> apply(upstream: Maybe<T>): Maybe<T> {
        val subscribed = subscribe?.let(upstream::subscribeOn) ?: upstream
        return observe?.let(subscribed::observeOn) ?: subscribed
    }

    fun apply(upstream: Completable): Completable {
        val subscribed = subscribe?.let(upstream::subscribeOn) ?: upstream
        return observe?.let(subscribed::observeOn) ?: subscribed
    }

    internal operator fun plus(schedulesApplier: SchedulesApplier): SchedulesApplier =
        SchedulesApplier(
            subscribe = this.subscribe ?: schedulesApplier.subscribe,
            observe = schedulesApplier.observe ?: this.observe
        )

}

fun <T> Flowable<T>.applySchedules(applier: SchedulesApplier): Flowable<T> {
    return if (this is SchedulesApplierFlowable) {
        SchedulesApplierFlowable(this.applier + applier, this.upstream)
    } else {
        SchedulesApplierFlowable(applier, this)
    }
}

fun <T> Observable<T>.applySchedules(applier: SchedulesApplier): Observable<T> {
    return if (this is SchedulesApplierObservable) {
        SchedulesApplierObservable(this.applier + applier, this.upstream)
    } else {
        SchedulesApplierObservable(applier, this)
    }
}

fun <T> Single<T>.applySchedules(applier: SchedulesApplier): Single<T> {
    return if (this is SchedulesApplierSingle) {
        SchedulesApplierSingle(this.applier + applier, this.upstream)
    } else {
        SchedulesApplierSingle(applier, this)
    }
}

fun <T> Maybe<T>.applySchedules(applier: SchedulesApplier): Maybe<T> {
    return if (this is SchedulesApplierMaybe) {
        SchedulesApplierMaybe(this.applier + applier, this.upstream)
    } else {
        SchedulesApplierMaybe(applier, this)
    }
}

fun Completable.applySchedules(applier: SchedulesApplier): Completable {
    return if (this is SchedulesApplierCompletable) {
        SchedulesApplierCompletable(this.applier + applier, this.upstream)
    } else {
        SchedulesApplierCompletable(applier, this)
    }
}

private class SchedulesApplierFlowable<T>(
    internal var applier: SchedulesApplier,
    internal val upstream: Flowable<T>
): Flowable<T>() {

    override fun subscribeActual(observer: Subscriber<in T>) {

        upstream
            .applySchedules(applier)
            .subscribe(observer)

    }

}

private class SchedulesApplierObservable<T>(
    internal var applier: SchedulesApplier,
    internal val upstream: Observable<T>
): Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {

        upstream
            .applySchedules(applier)
            .subscribe(observer)

    }

}

private class SchedulesApplierSingle<T>(
    internal var applier: SchedulesApplier,
    internal val upstream: Single<T>
): Single<T>() {

    override fun subscribeActual(observer: SingleObserver<in T>) {

        upstream
            .applySchedules(applier)
            .subscribe(observer)

    }

}

private class SchedulesApplierMaybe<T>(
    internal var applier: SchedulesApplier,
    internal val upstream: Maybe<T>
): Maybe<T>() {

    override fun subscribeActual(observer: MaybeObserver<in T>) {

        upstream
            .applySchedules(applier)
            .subscribe(observer)

    }

}

private class SchedulesApplierCompletable(
    internal var applier: SchedulesApplier,
    internal val upstream: Completable
): Completable() {

    override fun subscribeActual(observer: CompletableObserver) {

        upstream
            .applySchedules(applier)
            .subscribe(observer)

    }

}