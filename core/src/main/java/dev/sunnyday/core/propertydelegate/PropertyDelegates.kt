package dev.sunnyday.core.propertydelegate

import dev.sunnyday.core.util.ReferenceWrapper
import dev.sunnyday.core.util.Soft
import dev.sunnyday.core.util.Weak
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */

fun <T: Any> weak(value: T? = null): ReadWriteProperty<Any, T?> =
    ReferenceWrapperProperty(Weak(value))

fun <T: Any> soft(value: T? = null): ReadWriteProperty<Any, T?> =
    ReferenceWrapperProperty(Soft(value))

private class ReferenceWrapperProperty<T: Any>(
    wrapper: ReferenceWrapper<T>
): ReadWriteProperty<Any, T?> {

    private val valueWrapper = wrapper

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = valueWrapper.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        valueWrapper.value = value
    }

}