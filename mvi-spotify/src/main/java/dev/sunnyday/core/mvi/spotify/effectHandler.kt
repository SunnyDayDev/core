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
private typealias Handling<F, E> = (effects: Observable<F>) -> ObservableSource<E>

fun <F: Any, E: Any> effectsHandler(
    handle: (effects: Observable<F>) -> ObservableSource<E>
): ObservableTransformer<F, E> = ObservableTransformer(handle)

fun <F: Any, E: Any> flat(handle: Handle<F, E>): Handling<F, E>  = { it.flatMap(handle) }

fun <F: Any, E: Any> concat(handle: Handle<F, E>): Handling<F, E> = { it.concatMap(handle) }

fun <F: Any, E: Any> switch(handle: Handle<F, E>): Handling<F, E>  = { it.switchMap(handle) }

fun <F: Any, E: Any> throttle(handle: Handle<F, E>): Handling<F, E> = { it.throttleMap(handle) }

fun <F: Any, E: Any> flatEffectHandler(handle: Handle<F, E>) = effectsHandler(flat(handle))

fun <F: Any, E: Any> concatEffectHandler(handle: Handle<F, E>) = effectsHandler(concat(handle))

fun <F: Any, E: Any> switchEffectHandler(handle: Handle<F, E>) = effectsHandler(switch(handle))

fun <F: Any, E: Any> throttleEffectHandler(handle: Handle<F, E>) = effectsHandler(throttle(handle))