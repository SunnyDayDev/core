package dev.sunnyday.core.propertydelegate

import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <O, T> propertyOf(owner: O, prop: ReadWriteProperty<O, T>) = object :
    ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T = prop.getValue(owner, property)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) =
        prop.setValue(owner, property, value)

}

fun <O, T> propertyOf(owner: O, prop: ReadOnlyProperty<O, T>) =
    ReadOnlyProperty<Any, T> { _, property -> prop.getValue(owner, property) }