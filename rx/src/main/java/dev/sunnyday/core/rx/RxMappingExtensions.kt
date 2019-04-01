package dev.sunnyday.core.rx

import io.reactivex.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 02.08.2018.
 * mail: mail@sunnyday.dev
 */

// region: Map list

fun <T, R> Flowable<Iterable<T>>.mapList(mapper: (T) -> R): Flowable<List<R>> = map { it.map(mapper) }

fun <T, R> Observable<Iterable<T>>.mapList(mapper: (T) -> R): Observable<List<R>> = map { it.map(mapper) }

fun <T, R> Single<Iterable<T>>.mapList(mapper: (T) -> R): Single<List<R>> = map { it.map(mapper) }

fun <T, R> Maybe<Iterable<T>>.mapList(mapper: (T) -> R): Maybe<List<R>> = map { it.map(mapper) }

fun <T, C: MutableCollection<in R>, R> Flowable<Iterable<T>>.mapListTo(collection: C, mapper: (T) -> R): Flowable<C> = map { it.mapTo(collection, mapper) }

fun <T, C: MutableCollection<in R>, R> Observable<Iterable<T>>.mapListTo(collection: C, mapper: (T) -> R): Observable<C> = map { it.mapTo(collection, mapper) }

fun <T, C: MutableCollection<in R>, R> Single<Iterable<T>>.mapListTo(collection: C, mapper: (T) -> R): Single<C> = map { it.mapTo(collection, mapper) }

fun <T, C: MutableCollection<in R>, R> Maybe<Iterable<T>>.mapListTo(collection: C, mapper: (T) -> R): Maybe<C> = map { it.mapTo(collection, mapper) }

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

fun <T: Any, R: Any> Observable<T>.throttleMap(map: (T) -> ObservableSource<out R>): Observable<R> {

    val mapping = AtomicBoolean(false)

    return flatMap {

        if (mapping.getAndSet(true)) {

            Observable.empty<R>()

        } else {

            Observable.just(it)
                .flatMap(map)
                .doFinally { mapping.set(false) }

        }

    }

}

fun <T: Any, R: Any> Observable<T>.throttleMapSingle(map: (T) -> SingleSource<out R>): Observable<R> {

    val mapping = AtomicBoolean(false)

    return flatMap {

        if (mapping.getAndSet(true)) {

            Observable.empty()

        } else {

            Single.defer { map(it) }
                .doFinally { mapping.set(false) }
                .toObservable()

        }

    }

}

fun <T: Any, R: Any> Observable<T>.throttleMapMaybe(map: (T) -> MaybeSource<out R>): Observable<R> {

    val mapping = AtomicBoolean(false)

    return flatMapMaybe {

        if (mapping.getAndSet(true)) {

            Maybe.empty()

        } else {

            Maybe.defer { map(it) }
                .doFinally { mapping.set(false) }

        }

    }

}

fun <T> Observable<T>.throttleMapCompletable(map: (T) -> CompletableSource): Completable {

    val mapping = AtomicBoolean(false)

    return flatMapCompletable {

        if (mapping.getAndSet(true)) {

            Completable.complete()

        } else {

            Completable.defer { map(it) }
                .doFinally { mapping.set(false) }

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