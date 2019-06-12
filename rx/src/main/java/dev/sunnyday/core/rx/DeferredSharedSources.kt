package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.currentStackTraceElement
import dev.sunnyday.core.util.Late
import io.reactivex.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-12.
 * mail: mail@sunnydaydev.me
 */

class DeferredSharedSources {

    private val flowables by lazy { mutableMapOf<Any, Flowable<*>>() }
    private val observables by lazy { mutableMapOf<Any, Observable<*>>() }
    private val singles by lazy { mutableMapOf<Any, Single<*>>() }
    private val maybes by lazy { mutableMapOf<Any, Maybe<*>>() }
    private val completables by lazy { mutableMapOf<Any, Completable>() }
    
    fun <T> flowable(key: Any = defaultSharedSourceKey(),
                     replayCount: Int = 1,
                     creator: () -> Flowable<T>): Flowable<T> = Flowable.defer {

        @Suppress("UNCHECKED_CAST")
        (flowables[key] as? Flowable<T>)?.let {
            return@defer it
        }

        val late = Late<Flowable<T>>()

        late.value = creator()
            .doFinally {
                if (flowables[key] === late.value) {
                    flowables.remove(key)
                }
            }
            .replay(replayCount)
            .refCount()

        flowables[key] = late.value

        late.value
        
    }

    fun <T> observable(key: Any = defaultSharedSourceKey(),
                       replayCount: Int = 1,
                       creator: () -> Observable<T>): Observable<T> = Observable.defer {

        @Suppress("UNCHECKED_CAST")
        (observables[key] as? Observable<T>)?.let {
            return@defer it
        }

        val late = Late<Observable<T>>()

        late.value = creator()
            .doFinally {
                if (observables[key] === late.value) {
                    observables.remove(key)
                }
            }
            .replay(replayCount)
            .refCount()

        observables[key] = late.value

        late.value

    }

    fun <T> single(key: Any = defaultSharedSourceKey(),
                    creator: () -> Single<T>): Single<T> = Single.defer {

        @Suppress("UNCHECKED_CAST")
        (singles[key] as? Single<T>)?.let {
            return@defer it
        }

        val late = Late<Single<T>>()

        late.value = creator()
            .doFinally {
                if (singles[key] === late.value) {
                    singles.remove(key)
                }
            }
            .cache()

        singles[key] = late.value

        late.value

    }
    
    fun <T> maybe(key: Any = defaultSharedSourceKey(),
                  creator: () -> Maybe<T>): Maybe<T> = Maybe.defer {

        @Suppress("UNCHECKED_CAST")
        (maybes[key] as? Maybe<T>)?.let {
            return@defer it
        }

        val late = Late<Maybe<T>>()

        late.value = creator()
            .doFinally {
                if (maybes[key] === late.value) {
                    maybes.remove(key)
                }
            }
            .cache()

        maybes[key] = late.value

        late.value

    }
    
    fun completable(
        key: Any = defaultSharedSourceKey(),
        creator: () -> Completable
    ): Completable = Completable.defer {

        completables[key]?.let {
            return@defer it
        }

        val late = Late<Completable>()

        late.value = creator()
            .doFinally {
                if (completables[key] === late.value) {
                    completables.remove(key)
                }
            }
            .cache()

        completables[key] = late.value

        late.value

    }

    private fun defaultSharedSourceKey(): String = currentStackTraceElement(shift = 1).let {
        "${it.className}.${it.methodName}(${it.fileName}:${it.lineNumber})"
    }

}