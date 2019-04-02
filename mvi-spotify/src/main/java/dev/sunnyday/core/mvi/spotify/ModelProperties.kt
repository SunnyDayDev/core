package dev.sunnyday.core.mvi.spotify

import dev.sunnyday.core.util.equals
import com.spotify.mobius.functions.Consumer
import dev.sunnyday.core.util.Wrapped

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

    private lateinit var currentValue: Wrapped<P>

    override fun accept(model: M) {

        val newValue = property(model)

        if (!::currentValue.isInitialized || changed(currentValue.value, newValue)) {
            currentValue = Wrapped(newValue)
            onChange(newValue)
        }

    }

    private fun changed(current: P, new: P): Boolean = !(
            current === new || compare(current, new))

}