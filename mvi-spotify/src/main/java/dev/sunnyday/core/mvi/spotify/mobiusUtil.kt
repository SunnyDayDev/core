/*
 * -\-\-
 * --
 * Copyright (c) 2017-2018 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package dev.sunnyday.core.mvi.spotify

import com.spotify.mobius.*
import com.spotify.mobius.functions.Consumer
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.ObservableTransformer

private fun <M, E, F> updateWrapper(update: (M, E) -> Next<M, F>): Update<M, E, F> =
        Update { m: M, e: E -> update(m, e) }

fun <M, E, F> loopFactory(
        update: (M, E) -> Next<M, F>,
        effectHandlers: ObservableTransformer<F, E>
) = RxMobius.loop(updateWrapper(update), effectHandlers)

class PartialConnection<T>(val onModelChange: (T) -> Unit) {
    fun onDispose(dispose: () -> Unit): Connection<T> = object : Connection<T> {
        override fun accept(value: T) {
            onModelChange(value)
        }

        override fun dispose() {
            dispose()
        }
    }
}

fun <T> onAccept(onModelChange: (T) -> Unit): PartialConnection<T> =
        PartialConnection(onModelChange)

fun <I, J, O> Connectable<I, O>.contramap(block: (J) -> I) = ContramapConnectable(block, this)

class ContramapConnectable<I, J, O> (
        private val map: (J) -> I,
        private val delegate: Connectable<I, O>) : Connectable<J, O> {
    override fun connect(output: Consumer<O>): Connection<J> {
        val connection = delegate.connect(output)
        return onAccept<J> { connection.accept(map(it)) }
                .onDispose { connection.dispose() }
    }
}

inline fun <M, F> Next<M, F>.changeModel(change: (current: M?) -> M): Next<M, F> {

    val newModel = if (hasModel()) change(modelUnsafe())
                   else change(null)

    return Next.next(newModel, effects())

}

inline fun <M, F> Next<M, F>.changeEffects(change: (Set<F>) -> Set<F>): Next<M, F> {

    val newEffects = change(effects())

    return if (hasModel()) Next.next(modelUnsafe(), newEffects)
    else Next.dispatch(newEffects)

}

fun <E> effects(vararg effects: E): Set<E> {
    return com.spotify.mobius.Effects.effects(*effects)
}