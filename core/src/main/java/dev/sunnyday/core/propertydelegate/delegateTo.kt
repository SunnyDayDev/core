package dev.sunnyday.core.propertydelegate

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

fun <T> delegateTo(propertyDelegate: KMutableProperty0<T>): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = propertyDelegate.get()

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) = propertyDelegate.set(value)

}

fun <T> delegateTo(propertyDelegate: KProperty0<T>): ReadOnlyProperty<Any, T> = object : ReadOnlyProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = propertyDelegate.get()

}