package dev.sunnyday.core.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */
 
fun <T> weak(creator: () -> T) = WeakProperty(creator)

class WeakProperty<T> constructor(
        private val creator: () -> T
): ReadWriteProperty<Any, T> {

    private val weakValue = Weak<T>()

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
            weakValue.getOrSet { creator() }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        weakValue.value = value
    }

}