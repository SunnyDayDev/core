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

fun ioToBackground(schedulers: AppSchedulers): CombinedSchedules =
    CombinedSchedules(subscribe = schedulers.io, observe = schedulers.background)

fun ioToUI(schedulers: AppSchedulers): CombinedSchedules =
    CombinedSchedules(subscribe = schedulers.io, observe = schedulers.ui)

fun backgroundToUI(schedulers: AppSchedulers): CombinedSchedules =
    CombinedSchedules(subscribe = schedulers.background, observe = schedulers.ui)

fun ui(schedulers: AppSchedulers): CombinedSchedules =
    CombinedSchedules(observe = schedulers.ui)

data class CombinedSchedules(
    val subscribe: Scheduler? = null,
    val observe: Scheduler? = null
) {

    companion object {

        fun subscribe(scheduler: Scheduler) = CombinedSchedules(subscribe = scheduler)

        fun observe(scheduler: Scheduler) = CombinedSchedules(observe = scheduler)

    }

    internal operator fun plus(schedulesApplier: CombinedSchedules): CombinedSchedules =
        CombinedSchedules(
            subscribe = this.subscribe ?: schedulesApplier.subscribe,
            observe = schedulesApplier.observe ?: this.observe
        )

}

fun <T> Flowable<T>.applySchedules(schedules: CombinedSchedules): Flowable<T> {
    return if (this is SchedulesApplierFlowable) {
        SchedulesApplierFlowable(this.schedules + schedules, this.upstream)
    } else {
        SchedulesApplierFlowable(schedules, this)
    }
}

fun <T> Observable<T>.applySchedules(schedules: CombinedSchedules): Observable<T> {
    return if (this is SchedulesApplierObservable) {
        SchedulesApplierObservable(this.schedules + schedules, this.upstream)
    } else {
        SchedulesApplierObservable(schedules, this)
    }
}

fun <T> Single<T>.applySchedules(schedules: CombinedSchedules): Single<T> {
    return if (this is SchedulesApplierSingle) {
        SchedulesApplierSingle(this.schedules + schedules, this.upstream)
    } else {
        SchedulesApplierSingle(schedules, this)
    }
}

fun <T> Maybe<T>.applySchedules(schedules: CombinedSchedules): Maybe<T> {
    return if (this is SchedulesApplierMaybe) {
        SchedulesApplierMaybe(this.schedules + schedules, this.upstream)
    } else {
        SchedulesApplierMaybe(schedules, this)
    }
}

fun Completable.applySchedules(schedules: CombinedSchedules): Completable {
    return if (this is SchedulesApplierCompletable) {
        SchedulesApplierCompletable(this.schedules + schedules, this.upstream)
    } else {
        SchedulesApplierCompletable(schedules, this)
    }
}

private class SchedulesApplierFlowable<T>(
    internal var schedules: CombinedSchedules,
    internal val upstream: Flowable<T>
): Flowable<T>() {

    override fun subscribeActual(observer: Subscriber<in T>) {

        upstream
            .let { schedules.subscribe?.let(it::subscribeOn) ?:  it }
            .let { schedules.observe?.let(it::observeOn) ?:  it }
            .subscribe(observer)

    }

}

private class SchedulesApplierObservable<T>(
    internal var schedules: CombinedSchedules,
    internal val upstream: Observable<T>
): Observable<T>() {

    override fun subscribeActual(observer: Observer<in T>) {

        upstream
            .let { schedules.subscribe?.let(it::subscribeOn) ?:  it }
            .let { schedules.observe?.let(it::observeOn) ?:  it }
            .subscribe(observer)

    }

}

private class SchedulesApplierSingle<T>(
    internal var schedules: CombinedSchedules,
    internal val upstream: Single<T>
): Single<T>() {

    override fun subscribeActual(observer: SingleObserver<in T>) {

        upstream
            .let { schedules.subscribe?.let(it::subscribeOn) ?:  it }
            .let { schedules.observe?.let(it::observeOn) ?:  it }
            .subscribe(observer)

    }

}

private class SchedulesApplierMaybe<T>(
    internal var schedules: CombinedSchedules,
    internal val upstream: Maybe<T>
): Maybe<T>() {

    override fun subscribeActual(observer: MaybeObserver<in T>) {

        upstream
            .let { schedules.subscribe?.let(it::subscribeOn) ?:  it }
            .let { schedules.observe?.let(it::observeOn) ?:  it }
            .subscribe(observer)

    }

}

private class SchedulesApplierCompletable(
    internal var schedules: CombinedSchedules,
    internal val upstream: Completable
): Completable() {

    override fun subscribeActual(observer: CompletableObserver) {

        upstream
            .let { schedules.subscribe?.let(it::subscribeOn) ?:  it }
            .let { schedules.observe?.let(it::observeOn) ?:  it }
            .subscribe(observer)

    }

}