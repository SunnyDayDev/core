package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.util.equals
import com.spotify.mobius.functions.Consumer

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2018-12-28.
 * mail: mail@sunnydaydev.me
 */
 
class ModelProperties<M: Any> : Consumer<M> {

    private val properties = mutableListOf<ModelProperty<M, *>>()

    fun <P> register(
        property: (M) -> P,
        compare: (P, P) -> Boolean = Any?::equals,
        onChange: (P) -> Unit
    ): ModelProperty<M, P> = ModelProperty(property, compare, onChange).also(::register)

    fun <P> register(property: ModelProperty<M, P>) {
        properties.add(property)
    }

    fun remove(property: ModelProperty<M, *>) {
        properties.remove(property)
    }

    override fun accept(model: M) {
        properties.forEach { it.accept(model) }
    }

}

class ModelProperty<M, P>(
    val property: (M) -> P,
    val compare: (P, P) -> Boolean = Any?::equals,
    val onChange: (P) -> Unit
): Consumer<M> {

    private var value: Value<P> = Value.NotInitialized()

    override fun accept(model: M) {

        val newValue = property(model)
        val currentValue = value

        if (currentValue is Value.NotInitialized ||
            currentValue is Value.Initialized && !compare(currentValue.value, newValue)) {
            value = Value.Initialized(newValue)
            onChange(newValue)
        }

    }

    @Suppress("unused")
    private sealed class Value<T> {
        class NotInitialized<T>: Value<T>()
        data class Initialized<T>(val value: T): Value<T>()
    }

}