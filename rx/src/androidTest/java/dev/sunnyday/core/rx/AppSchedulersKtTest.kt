package dev.sunnyday.core.rx

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.NewThreadWorker
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-05-27.
 * mail: mail@sunnydaydev.me
 */

@RunWith(AndroidJUnit4::class)
class AppSchedulersKtTest {

    @Test
    fun testDefaultAppSchedulers() {

        val schedulers: AppSchedulers = AppSchedulers.Default()

        assertTrue(schedulers.background === Schedulers.computation())
        assertTrue(schedulers.ui === AndroidSchedulers.mainThread())
        assertTrue(schedulers.io === Schedulers.io())

    }

    @Test
    fun ioToBackground() {

        val schedulers = TestAppSchedulers()

        val applier = ioToBackground(schedulers)
        assertTrue(applier.subscribe === schedulers.io)
        assertTrue(applier.observe === schedulers.background)

    }

    @Test
    fun ioToUI() {

        val schedulers = TestAppSchedulers()

        val applier = ioToUI(schedulers)
        assertTrue(applier.subscribe === schedulers.io)
        assertTrue(applier.observe === schedulers.ui)

    }

    @Test
    fun backgroundToUI() {

        val schedulers = TestAppSchedulers()

        val applier = backgroundToUI(schedulers)
        assertTrue(applier.subscribe === schedulers.background)
        assertTrue(applier.observe === schedulers.ui)

    }

    @Test
    fun ui() {

        val schedulers = TestAppSchedulers()

        val applier = ui(schedulers)
        assertTrue(applier.subscribe === null)
        assertTrue(applier.observe === schedulers.ui)

    }

    @Test
    fun applySchedulesFlowable() {

        val schedulers = TestAppSchedulers()

        var subscribedOnBackground = false
        var observedOnUI = false

        Flowable
            .create<Unit>({ emitter ->
                subscribedOnBackground = schedulers.background.activeThreads.contains(Thread.currentThread())
                emitter(Unit)
                emitter()
            }, BackpressureStrategy.LATEST)
            .applySchedules(backgroundToUI(schedulers))
            .doOnNext {
                observedOnUI = schedulers.ui.activeThreads.contains(Thread.currentThread())
            }
            .blockingLast()

        assertTrue(subscribedOnBackground)
        assertTrue(observedOnUI)

    }

    @Test
    fun applySchedulesObservable() {

        val schedulers = TestAppSchedulers()

        var subscribedOnIO = false
        var observedOnBackground = false

        Observable
            .create<Unit> { emitter ->
                subscribedOnIO = schedulers.io.activeThreads.contains(Thread.currentThread())
                emitter(Unit)
                emitter()
            }
            .applySchedules(ioToBackground(schedulers))
            .doOnNext {
                observedOnBackground = schedulers.background.activeThreads.contains(Thread.currentThread())
            }
            .blockingLast()

        assertTrue(subscribedOnIO)
        assertTrue(observedOnBackground)

    }

    @Test
    fun applySchedulesSingle() {

        val schedulers = TestAppSchedulers()

        var subscribedOnIO = false
        var observedOnUI = false

        Single
            .create<Unit> { emitter ->
                subscribedOnIO = schedulers.io.activeThreads.contains(Thread.currentThread())
                emitter(Unit)
            }
            .applySchedules(ioToUI(schedulers))
            .doOnSuccess {
                observedOnUI = schedulers.ui.activeThreads.contains(Thread.currentThread())
            }
            .blockingGet()

        assertTrue(subscribedOnIO)
        assertTrue(observedOnUI)

    }

    @Test
    fun applySchedulesMaybe() {

        val schedulers = TestAppSchedulers()

        var subscribedOnCurrentThread = false
        var observedOnUI = false

        val thread = Thread.currentThread()

        Maybe
            .create<Unit> { emitter ->
                subscribedOnCurrentThread = thread === Thread.currentThread()
                emitter(Unit)
            }
            .applySchedules(ui(schedulers))
            .doOnSuccess {
                observedOnUI = schedulers.ui.activeThreads.contains(Thread.currentThread())
            }
            .blockingGet()

        assertTrue(subscribedOnCurrentThread)
        assertTrue(observedOnUI)

    }

    @Test
    fun applySchedulesCompletable() {

        val schedulers = TestAppSchedulers()

        var subscribedOnBackground = false
        var observedOnUI = false

        Completable.create { emitter ->
                subscribedOnBackground = schedulers.background.activeThreads.contains(Thread.currentThread())
                emitter()
            }
            .applySchedules(backgroundToUI(schedulers))
            .doOnComplete {
                observedOnUI = schedulers.ui.activeThreads.contains(Thread.currentThread())
            }
            .blockingGet()

        assertTrue(subscribedOnBackground)
        assertTrue(observedOnUI)

    }

    @Test
    fun applySchedulesChainUseBestSchedules() {

        val schedulers = TestAppSchedulers()

        Completable.create { emitter -> emitter() }
            .applySchedules(ioToBackground(schedulers))
            .applySchedules(backgroundToUI(schedulers))
            .applySchedules(ui(schedulers))
            .blockingGet()

        assertTrue(schedulers.io.executedThreadsCount == 1)
        assertTrue(schedulers.background.executedThreadsCount == 0)
        assertTrue(schedulers.ui.executedThreadsCount == 1)

    }

    @Test
    fun schedulesChainWithoutApplySchedulesUseNotBestSchedules() {

        val schedulers = TestAppSchedulers()

        Completable.create { emitter -> emitter() }
            // equals to applySchedules(ioToBackground(schedulers))
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.background)
            // equals to applySchedules(backgroundToUI(schedulers))
            .subscribeOn(schedulers.background)
            .observeOn(schedulers.ui)
            // equals to applySchedules(ui(schedulers))
            .observeOn(schedulers.ui)
            .blockingGet()

        assertTrue(schedulers.io.executedThreadsCount == 1)
        assertTrue(schedulers.background.executedThreadsCount == 2)
        assertTrue(schedulers.ui.executedThreadsCount == 2)

    }

    @Test
    fun combinedSchedulesSubscribe() {

        val scheduler = TestScheduler()

        val combined = CombinedSchedules.subscribe(scheduler)

        assertTrue(combined.observe == null)
        assertTrue(combined.subscribe === scheduler)

    }

    @Test
    fun combinedSchedulesObserve() {

        val scheduler = TestScheduler()

        val combined = CombinedSchedules.observe(scheduler)

        assertTrue(combined.observe == scheduler)
        assertTrue(combined.subscribe == null)

    }

    private class TestAppSchedulers: AppSchedulers {

        override val background = TestScheduler()
        override val ui = TestScheduler()
        override val io = TestScheduler()

    }

    class TestScheduler: Scheduler() {

        val activeThreads: MutableList<Thread> = mutableListOf()

        var executedThreadsCount = 0

        override fun createWorker(): Worker = NewThreadWorker { runnable ->
            Thread {
                executedThreadsCount++
                activeThreads.add(Thread.currentThread())
                runnable.run()
                activeThreads.remove(Thread.currentThread())
            }
        }

    }

}