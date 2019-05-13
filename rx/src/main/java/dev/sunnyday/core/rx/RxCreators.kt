package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.currentStackTraceElement
import dev.sunnyday.core.util.Late
import io.reactivex.*
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.Maybes
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-20.
 * mail: mail@sunnyday.dev
 */

fun <T> Observables.combineLatest(sources: Iterable<Observable<T>>): Observable<List<T>> {

    if (sources.none()) return Observable.just(emptyList())

    return Observable.combineLatest(sources) { results ->
        results.map {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

}

fun <T> Singles.zip(sources: Iterable<Single<T>>): Single<List<T>> {

    if (sources.none()) return Single.just(emptyList())

    return Single.zip(sources) { results ->
        results.map {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

}

fun <T> Singles.concat(list: List<Single<T>>): Flowable<T> {
    return Single.concat(list)
}

// region Cached while not terminated

fun <T> Flowables.replayWhileNotTerminated(
    store: MutableMap<Any, Flowable<*>>,
    replayCount: Int = 1,
    key: Any = defaultSharedSourceKey(),
    creator: () -> Flowable<T>
): Flowable<T> = Flowable.defer {

    (store[key] as? Flowable<T>)?.let {
        return@defer it
    }

    val late = Late<Flowable<T>>()

    late.value = creator()
        .doFinally {
            if (store[key] === late.value) {
                store.remove(key)
            }
        }
        .replay(replayCount)
        .refCount()

    store[key] = late.value

    late.value

}

fun <T> Observables.replayWhileNotTerminated(
    store: MutableMap<Any, Observable<*>>,
    replayCount: Int = 1,
    key: Any = defaultSharedSourceKey(),
    creator: () -> Observable<T>
): Observable<T> = Observable.defer {

    (store[key] as? Observable<T>)?.let {
        return@defer it
    }

    val late = Late<Observable<T>>()

    late.value = creator()
        .doFinally {
            if (store[key] === late.value) {
                store.remove(key)
            }
        }
        .replay(replayCount)
        .refCount()

    store[key] = late.value

    late.value

}

fun <T> Singles.cachedWhileNotTerminated(
    store: MutableMap<Any, Single<*>>,
    key: Any = defaultSharedSourceKey(),
    creator: () -> Single<T>
): Single<T> = Single.defer {

    (store[key] as? Single<T>)?.let {
        return@defer it
    }

    val late = Late<Single<T>>()

    late.value = creator()
        .doFinally {
            if (store[key] === late.value) {
                store.remove(key)
            }
        }
        .cache()

    store[key] = late.value

    late.value

}

fun <T> Maybes.cachedWhileNotTerminated(
    store: MutableMap<Any, Maybe<*>>,
    key: Any = defaultSharedSourceKey(),
    creator: () -> Maybe<T>
): Maybe<T> = Maybe.defer {

    (store[key] as? Maybe<T>)?.let {
        return@defer it
    }

    val late = Late<Maybe<T>>()

    late.value = creator()
        .doFinally {
            if (store[key] === late.value) {
                store.remove(key)
            }
        }
        .cache()

    store[key] = late.value

    late.value

}

object Completables {
    
    fun cachedWhileNotTerminated(
        store: MutableMap<Any, Completable>,
        key: Any = defaultSharedSourceKey(),
        creator: () -> Completable
    ): Completable = Completable.defer {

        store[key]?.let {
            return@defer it
        }

        val late = Late<Completable>()

        late.value = creator()
            .doFinally {
                if (store[key] === late.value) {
                    store.remove(key)
                }
            }
            .cache()

        store[key] = late.value

        late.value

    }
    
}

// endregion

private fun defaultSharedSourceKey(): String {
    return currentStackTraceElement(shift = 1).let { "${it.fileName}:${it.lineNumber}" }
}