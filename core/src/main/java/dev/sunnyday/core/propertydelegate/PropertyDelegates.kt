package dev.sunnyday.core.propertydelegate

import dev.sunnyday.core.util.Soft
import dev.sunnyday.core.util.Weak
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */

fun <T: Any> weak(creator: (() -> T?)? = null) = WeakProperty(creator)

fun <T: Any> soft(creator: (() -> T?)? = null) = SoftProperty(creator)

class WeakProperty<T: Any> constructor(
    private val creator: (() -> T?)? = null
): ReadWriteProperty<Any, T?> {

    private val weakValue = Weak<T>()

    override fun getValue(thisRef: Any, property: KProperty<*>): T? =
        creator?.let(weakValue::getOrSet) ?: weakValue.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        weakValue.value = value
    }

}

class SoftProperty<T: Any> constructor(
    private val creator: (() -> T?)? = null
): ReadWriteProperty<Any, T?> {

    private val weakValue = Soft<T>()

    override fun getValue(thisRef: Any, property: KProperty<*>): T? =
        creator?.let(weakValue::getOrSet) ?: weakValue.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        weakValue.value = value
    }

}