package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.rx.throttleMap
import dev.sunnyday.core.rx.throttleMapCompletable
import dev.sunnyday.core.rx.throttleMapMaybe
import dev.sunnyday.core.rx.throttleMapSingle
import io.reactivex.*

import io.reactivex.functions.Function as RxFunction

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

private typealias Handle<F, E> = (effect: F) -> ObservableSource<out E>
private typealias EmptyHandle<E> = () -> ObservableSource<out E>
private typealias Handling<F, E> = (effects: Observable<F>) -> ObservableSource<E>

fun <F: Any, E: Any> effectsHandler(handle: Handling<F, E>): ObservableTransformer<F, E> = ObservableTransformer(handle)

fun <F: Any, E: Any> flat(handle: Handle<F, E>): Handling<F, E> = { it.flatMap(handle) }

fun <F: Any, E: Any> concat(handle: Handle<F, E>): Handling<F, E> = { it.concatMap(handle) }

fun <F: Any, E: Any> switch(handle: Handle<F, E>): Handling<F, E> = { it.switchMap(handle) }

fun <F: Any, E: Any> throttle(handle: Handle<F, E>): Handling<F, E> = { it.throttleMap(handle) }

inline fun <F: Any, E: Any> flat(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.flatMap { handle() } }

inline fun <F: Any, E: Any> concat(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.concatMap { handle() } }

inline fun <F: Any, E: Any> switch(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.switchMap { handle() } }

inline fun <F: Any, E: Any> throttle(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.throttleMap { handle() } }

@JvmName("flatSingle")
fun <T: Any, R: Any> flat(transformer: (T) -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapSingle(transformer)
}

@JvmName("flatSingle")
fun <T: Any, R: Any> flat(transformer: () -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapSingle { transformer() }
}

@JvmName("flatMaybe")
fun <T: Any, R: Any> flat(transformer: (T) -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapMaybe(transformer)
}

@JvmName("flatMaybe")
fun <T: Any, R: Any> flat(creator: () -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapMaybe { creator() }
}

@JvmName("flatCompletable")
fun <T: Any, R: Any> flat(creator: (T) -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapCompletable(creator) .toObservable()
}

@JvmName("flatCompletable")
fun <T: Any, R: Any> flat(creator: () -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.flatMapCompletable { creator() } .toObservable()
}

@JvmName("concatSingle")
fun <T: Any, R: Any> concat(creator: (T) -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapSingle(creator)
}

@JvmName("concatSingle")
fun <T: Any, R: Any> concat(creator: () -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapSingle { creator() }
}

@JvmName("concatMaybe")
fun <T: Any, R: Any> concat(transformer: (T) -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapMaybe(transformer)
}

@JvmName("concatMaybe")
fun <T: Any, R: Any> concat(creator: () -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapMaybe { creator() }
}

@JvmName("concatCompletable")
fun <T: Any, R: Any> concat(transformer: (T) -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapCompletable(transformer)
        .toObservable()
}

@JvmName("concatCompletable")
fun <T: Any, R: Any> concat(creator: () -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.concatMapCompletable { creator() }
        .toObservable()
}

@JvmName("switchSingle")
fun <T: Any, R: Any> switch(creator: (T) -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapSingle(creator)
}

@JvmName("switchSingle")
fun <T: Any, R: Any> switch(creator: () -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapSingle { creator() }
}

@JvmName("switchMaybe")
fun <T: Any, R: Any> switch(transformer: (T) -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapMaybe(transformer)
}

@JvmName("switchMaybe")
fun <T: Any, R: Any> switch(creator: () -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapMaybe { creator() }
}

@JvmName("switchCompletable")
fun <T: Any, R: Any> switch(transformer: (T) -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapCompletable(transformer)
        .toObservable()
}

@JvmName("switchCompletable")
fun <T: Any, R: Any> switch(creator: () -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.switchMapCompletable { creator() }
        .toObservable()
}

@JvmName("throttleSingle")
fun <T: Any, R: Any> throttle(creator: (T) -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapSingle(creator)
}

@JvmName("throttleSingle")
fun <T: Any, R: Any> throttle(creator: () -> SingleSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapSingle { creator() }
}

@JvmName("throttleMaybe")
fun <T: Any, R: Any> throttle(transformer: (T) -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapMaybe(transformer)
}

@JvmName("throttleMaybe")
fun <T: Any, R: Any> throttle(creator: () -> MaybeSource<out R>): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapMaybe { creator() }
}

@JvmName("throttleCompletable")
fun <T: Any, R: Any> throttle(transformer: (T) -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapCompletable(transformer)
        .toObservable()
}

@JvmName("throttleCompletable")
fun <T: Any, R: Any> throttle(creator: () -> Completable): ObservableTransformer<T, R> = ObservableTransformer {
    it.throttleMapCompletable { creator() }
        .toObservable()
}