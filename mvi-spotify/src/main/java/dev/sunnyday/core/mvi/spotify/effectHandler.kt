package dev.sunnyday.core.mvi.spotify

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

internal fun <F: Any, E: Any> effectsHandler(
    handle: (effects: Observable<F>) -> Observable<out E>
): ObservableTransformer<F, E> = ObservableTransformer { effects ->
    handle(effects).map { it }
}

internal inline fun <F: Any, E: Any> effectHandler(
    crossinline activation: Observable<F>.((F) -> ObservableSource<out E>) -> Observable<E> = Observable<F>::switchMap,
    noinline handle: (F) -> ObservableSource<out E>
): ObservableTransformer<F, E> = effectsHandler { effects ->
    activation(effects, handle)
}
