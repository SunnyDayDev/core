package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.rx.throttleMap
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

import io.reactivex.functions.Function as RxFunction

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

private typealias Handle<F, E> = (effect: F) -> ObservableSource<out E>
private typealias EmptyHandle<E> = () -> ObservableSource<out E>
private typealias Handling<F, E> = (effects: Observable<F>) -> ObservableSource<E>

fun <F: Any, E: Any> effectsHandler(handle: Handling<F, E>): ObservableTransformer<F, E> = ObservableTransformer(handle)

fun <F: Any, E: Any> flat(handle: Handle<F, E>): Handling<F, E>  = { it.flatMap(handle) }

fun <F: Any, E: Any> concat(handle: Handle<F, E>): Handling<F, E> = { it.concatMap(handle) }

fun <F: Any, E: Any> switch(handle: Handle<F, E>): Handling<F, E>  = { it.switchMap(handle) }

fun <F: Any, E: Any> throttle(handle: Handle<F, E>): Handling<F, E> = { it.throttleMap(handle) }

inline fun <F: Any, E: Any> flat(crossinline handle: EmptyHandle<E>): Handling<F, E>  = { it.flatMap { handle() } }

inline fun <F: Any, E: Any> concat(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.concatMap { handle() } }

inline fun <F: Any, E: Any> switch(crossinline handle: EmptyHandle<E>): Handling<F, E>  = { it.switchMap { handle() } }

inline fun <F: Any, E: Any> throttle(crossinline handle: EmptyHandle<E>): Handling<F, E> = { it.throttleMap { handle() } }