package dev.sunnyday.core.rx

import io.reactivex.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 02.08.2018.
 * mail: mail@sunnyday.dev
 */

// region: Map list

fun <T, R> Flowable<List<T>>.mapList(mapper: (T) -> R): Flowable<List<R>> = map { it.map(mapper) }

fun <T, R> Observable<List<T>>.mapList(mapper: (T) -> R): Observable<List<R>> = map { it.map(mapper) }

fun <T, R> Single<List<T>>.mapList(mapper: (T) -> R): Single<List<R>> = map { it.map(mapper) }

fun <T, R> Maybe<List<T>>.mapList(mapper: (T) -> R): Maybe<List<R>> = map { it.map(mapper) }

@JvmName("mapSet")
fun <T, R> Flowable<Set<T>>.mapList(mapper: (T) -> R): Flowable<Set<R>> = map { it.map(mapper).toSet() }

@JvmName("mapSet")
fun <T, R> Observable<Set<T>>.mapList(mapper: (T) -> R): Observable<Set<R>> = map { it.map(mapper).toSet() }

@JvmName("mapSet")
fun <T, R> Single<Set<T>>.mapList(mapper: (T) -> R): Single<Set<R>> = map { it.map(mapper).toSet() }

@JvmName("mapSet")
fun <T, R> Maybe<Set<T>>.mapList(mapper: (T) -> R): Maybe<Set<R>> = map { it.map(mapper).toSet() }

// endregion

// region: Map to signal

fun <T> Flowable<T>.mapToSignal(predicate: (T) -> Boolean): Flowable<Unit> =
        filter(predicate).map { Unit }

fun <T> Flowable<T>.mapToSignal(): Flowable<Unit> = map { Unit }

fun Flowable<Boolean>.mapToSignal(filterValue: Boolean): Flowable<Unit> =
        filter{ it == filterValue } .map { Unit }

fun <T> Observable<T>.mapToSignal(predicate: (T) -> Boolean): Observable<Unit> =
        filter(predicate).map { Unit }

fun <T> Observable<T>.mapToSignal(): Observable<Unit> = map { Unit }

fun Observable<Boolean>.mapToSignal(acceptableValue: Boolean): Observable<Unit> =
        filter{ it == acceptableValue } .map { Unit }

fun <T> Single<T>.mapToSignal(predicate: (T) -> Boolean): Maybe<Unit> =
        filter(predicate).map { Unit }

fun <T> Single<T>.mapToSignal(): Single<Unit> = map { Unit }

fun Single<Boolean>.mapToSignal(filterValue: Boolean): Maybe<Unit> =
        filter{ it == filterValue } .map { Unit }

fun <T> Maybe<T>.mapToSignal(predicate: (T) -> Boolean): Maybe<Unit> =
        filter(predicate).map { Unit }

fun <T> Maybe<T>.mapToSignal(): Maybe<Unit> = map { Unit }

fun Maybe<Boolean>.mapToSignal(filterValue: Boolean): Maybe<Unit> =
        filter{ it == filterValue } .map { Unit }

// endregion

// region: throttleMap

fun <T,R> Observable<T>.throttleMap(map: (T) -> Observable<R>): Observable<R> {

    val mapping = AtomicBoolean(false)

    return flatMap {
        if (mapping.getAndSet(true)) {
            Observable.empty()
        } else {
            map(it).doFinally { mapping.set(false) }
        }
    }

}

fun <T,R> Observable<T>.throttleMapSingle(map: (T) -> Single<R>): Observable<R> =
        throttleMap { map(it).toObservable() }

fun <T,R> Observable<T>.throttleMapMaybe(map: (T) -> Maybe<R>): Observable<R> {

    val mapping = AtomicBoolean(false)

    return flatMapMaybe {
        if (mapping.getAndSet(true)) {
            Maybe.empty()
        } else {
            map(it).doFinally { mapping.set(false) }
        }
    }

}

fun <T> Observable<T>.throttleMapCompletable(map: (T) -> Completable): Completable {

    val mapping = AtomicBoolean(false)

    return flatMapCompletable {
        if (mapping.getAndSet(true)) {
            Completable.complete()
        } else {
            map(it).doFinally { mapping.set(false) }
        }
    }

}

// endregion

// region: Map error

fun Completable.mapError(mapper: (Throwable) -> Throwable): Completable = onErrorResumeNext {
    Completable.error(mapper(it))
}

fun <T> Maybe<T>.mapError(
        mapper: (Throwable) -> Throwable
): Maybe<T> = onErrorResumeNext { e: Throwable ->
    Maybe.error(mapper(e))
}

fun <T> Single<T>.mapError(mapper: (Throwable) -> Throwable): Single<T> = onErrorResumeNext {
    Single.error(mapper(it))
}

fun <T> Observable<T>.mapError(
        mapper: (Throwable) -> Throwable
): Observable<T> = onErrorResumeNext { e: Throwable ->
    Observable.error<T>(mapper(e))
}

fun <T> Flowable<T>.mapError(
        mapper: (Throwable) -> Throwable
): Flowable<T> = onErrorResumeNext { e: Throwable ->
    Flowable.error<T>(mapper(e))
}

// endregion

// region : Map not null

fun <T: Any, R: Any> Observable<T>.mapNotNull(mapper: (T) -> R?): Observable<R> =
        flatMap {  value ->
            mapper(value)?.let { Observable.just(it) } ?: Observable.empty()
        }

fun <T: Any, R: Any> Single<T>.mapNotNull(mapper: (T) -> R?): Maybe<R> =
        flatMapMaybe {  value ->
            mapper(value)?.let { Maybe.just(it) } ?: Maybe.empty()
        }

inline fun <reified R: Any> Observable<*>.filter(): Observable<R> =
        mapNotNull { it as? R }

// endregion