package dev.sunnyday.core.rx

import io.reactivex.*
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import org.junit.Assert
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-12.
 * mail: mail@sunnydaydev.me
 */

class DeferredSharedSourcesTest {

    @Test
    fun flowable() {

        val creator = Shared()

        val first = creator.flowable()
        val second = creator.flowable(failIfCreateNew = true)
        val third = creator.flowable()

        var fistOnNextIs1 = false
        var secondOnNextIs1 = false
        var thirdOnNextIs2 = false

        first.subscribeBy { fistOnNextIs1 = it == 1 }
        second.subscribeBy { secondOnNextIs1 = it == 1 }

        creator.subject.onNext(1)

        third.subscribeBy { thirdOnNextIs2 = it == 2 }

        creator.subject.onNext(2)

        Assert.assertTrue(fistOnNextIs1)
        Assert.assertTrue(secondOnNextIs1)
        Assert.assertTrue(thirdOnNextIs2)

    }

    @Test
    fun observable() {

        val creator = Shared()

        val first = creator.observable()
        val second = creator.observable(failIfCreateNew = true)
        val third = creator.observable()

        var fistOnNextIs1 = false
        var secondOnNextIs1 = false
        var thirdOnNextIs2 = false

        first.subscribeBy { fistOnNextIs1 = it == 1 }
        second.subscribeBy { secondOnNextIs1 = it == 1 }

        creator.subject.onNext(1)

        third.subscribeBy { thirdOnNextIs2 = it == 2 }

        creator.subject.onNext(2)

        Assert.assertTrue(fistOnNextIs1)
        Assert.assertTrue(secondOnNextIs1)
        Assert.assertTrue(thirdOnNextIs2)

    }

    @Test
    fun single() {

        val creator = Shared()

        val first = creator.single()
        val second = creator.single(failIfCreateNew = true)
        val third = creator.single()

        var fistOnNextIs1 = false
        var secondOnNextIs1 = false
        var thirdOnNextIs2 = false

        first.subscribeBy { fistOnNextIs1 = it == 1 }
        second.subscribeBy { secondOnNextIs1 = it == 1 }

        creator.subject.onNext(1)

        third.subscribeBy { thirdOnNextIs2 = it == 2 }

        creator.subject.onNext(2)

        Assert.assertTrue(fistOnNextIs1)
        Assert.assertTrue(secondOnNextIs1)
        Assert.assertTrue(thirdOnNextIs2)

    }

    @Test
    fun maybe() {

        val creator = Shared()

        val first = creator.maybe()
        val second = creator.maybe(failIfCreateNew = true)
        val third = creator.maybe()

        var fistOnNextIs1 = false
        var secondOnNextIs1 = false
        var thirdOnNextIs2 = false

        first.subscribeBy { fistOnNextIs1 = it == 1 }
        second.subscribeBy { secondOnNextIs1 = it == 1 }

        creator.subject.onNext(1)

        third.subscribeBy { thirdOnNextIs2 = it == 2 }

        creator.subject.onNext(2)

        Assert.assertTrue(fistOnNextIs1)
        Assert.assertTrue(secondOnNextIs1)
        Assert.assertTrue(thirdOnNextIs2)

    }

    @Test
    fun completable() {

        val creator = Shared()

        val first = creator.completable()
        val second = creator.completable(failIfCreateNew = true)
        val third = creator.completable()

        var fistCompleted = false
        var secondCompleted = false
        var thirdCompleted = false

        first.subscribeBy { fistCompleted = true }
        second.subscribeBy { secondCompleted = true }

        creator.subject.onNext(1)

        third.subscribeBy { thirdCompleted = true }

        creator.subject.onNext(2)

        Assert.assertTrue(fistCompleted)
        Assert.assertTrue(secondCompleted)
        Assert.assertTrue(thirdCompleted)

    }

    private class Shared {

        private val shared = DeferredSharedSources()

        val subject = PublishSubject.create<Int>()

        fun flowable(failIfCreateNew: Boolean = false): Flowable<Int> = shared.flowable {
            if (failIfCreateNew) error("Can't create new")
            subject.take(1).toFlowable(BackpressureStrategy.LATEST)
        }

        fun observable(failIfCreateNew: Boolean = false): Observable<Int> = shared.observable {
            if (failIfCreateNew) error("Can't create new")
            subject.take(1)
        }

        fun single(failIfCreateNew: Boolean = false): Single<Int> = shared.single {
            if (failIfCreateNew) error("Can't create new")
            subject.take(1).firstOrError()
        }

        fun maybe(failIfCreateNew: Boolean = false): Maybe<Int> = shared.maybe {
            if (failIfCreateNew) error("Can't create new")
            subject.take(1).firstElement()
        }

        fun completable(failIfCreateNew: Boolean = false): Completable = shared.completable {
            if (failIfCreateNew) error("Can't create new")
            subject.take(1).ignoreElements()
        }

    }

}