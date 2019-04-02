package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.rx.throttleMap
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

fun <F: Any, E: Any> effectsHandler(
    handle: (effects: Observable<F>) -> Observable<out E>
): ObservableTransformer<F, E> = ObservableTransformer { effects ->
    handle(effects).map { it }
}

inline fun <F: Any, E: Any> effectHandler(
    crossinline activation: Observable<F>.((F) -> ObservableSource<out E>) -> Observable<E>,
    noinline handle: (F) -> ObservableSource<out E>
): ObservableTransformer<F, E> = effectsHandler { effects ->
    activation(effects, handle)
}

fun <F: Any, E: Any> flatEffectHandler(handle: (F) -> ObservableSource<out E>) =
    effectHandler(Observable<F>::flatMap, handle)

fun <F: Any, E: Any> concatEffectHandler(handle: (F) -> ObservableSource<out E>) =
    effectHandler(Observable<F>::concatMap, handle)

fun <F: Any, E: Any> switchEffectHandler(handle: (F) -> ObservableSource<out E>) =
    effectHandler(Observable<F>::switchMap, handle)

fun <F: Any, E: Any> throttleEffectHandler(handle: (F) -> ObservableSource<out E>) =
    effectHandler(Observable<F>::throttleMap, handle)