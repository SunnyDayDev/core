package dev.sunnyday.core.propertydelegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-07.
 * mail: mail@sunnydaydev.me
 */

class RequiredPropertyIsNull(name: String): Throwable("Required property is null: $name")

fun <R, T>required(delegate: ReadWriteProperty<R, T?>): ReadWriteProperty<R, T> =
    RequiredProperty(delegate)

internal class RequiredProperty<R, T>(
    private val delegate: ReadWriteProperty<R, T?>
): ReadWriteProperty<R, T> {

    override fun getValue(thisRef: R, property: KProperty<*>): T =
        delegate.getValue(thisRef, property) ?: throw RequiredPropertyIsNull(property.name)

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) =
        delegate.setValue(thisRef, property, value)

}