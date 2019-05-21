package dev.sunnyday.core.rx

import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers

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

fun ioToBackground(schedulers: AppSchedulers): ScheduleApplier =
    ScheduleApplier(schedulers.io, schedulers.background)

fun ioToUI(schedulers: AppSchedulers): ScheduleApplier =
    ScheduleApplier(schedulers.io, schedulers.ui)

fun backgroundToUI(schedulers: AppSchedulers): ScheduleApplier =
    ScheduleApplier(schedulers.background, schedulers.ui)

class ScheduleApplier(
    private val subscribe: Scheduler?,
    private val observe: Scheduler?
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

}

fun <T> Flowable<T>.applySchedules(applier: ScheduleApplier): Flowable<T> = compose(applier::apply)

fun <T> Observable<T>.applySchedules(applier: ScheduleApplier): Observable<T> = compose(applier::apply)

fun <T> Single<T>.applySchedules(applier: ScheduleApplier): Single<T> = compose(applier::apply)

fun <T> Maybe<T>.applySchedules(applier: ScheduleApplier): Maybe<T> = compose(applier::apply)

fun Completable.applySchedules(applier: ScheduleApplier): Completable = compose(applier::apply)