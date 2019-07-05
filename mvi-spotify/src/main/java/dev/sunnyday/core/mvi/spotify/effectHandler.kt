package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.rx.throttleMap
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