package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import android.os.Parcelable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnyday.dev
 */

fun bundleBoolean(
    name: String? = null,
    default: Boolean
) = object: BundleProperty<Boolean>(name) {

    override fun getValue(bundle: Bundle, name: String): Boolean = bundle.getBoolean(name)

    override fun setValue(bundle: Bundle, name: String, value: Boolean) = bundle.putBoolean(name, value)

    override fun getUnexistsValue(): Boolean = default

}

fun bundleBoolean(
        name: String? = null
) = object : OptionalBundleProperty<Boolean>(name) {

    override fun getValue(bundle: Bundle, name: String): Boolean? = bundle.getBoolean(name)

    override fun setValue(bundle: Bundle, name: String, value: Boolean) = bundle.putBoolean(name, value)

}

fun bundleInt(
    name: String? = null,
    default: Int
) = object: BundleProperty<Int>(name) {

    override fun getValue(bundle: Bundle, name: String): Int = bundle.getInt(name)

    override fun setValue(bundle: Bundle, name: String, value: Int) = bundle.putInt(name, value)

    override fun getUnexistsValue(): Int = default

}

fun bundleInt(
    name: String? = null
) = object: OptionalBundleProperty<Int>(name) {

    override fun getValue(bundle: Bundle, name: String): Int = bundle.getInt(name)

    override fun setValue(bundle: Bundle, name: String, value: Int) = bundle.putInt(name, value)

}

fun bundleLong(
    name: String? = null,
    default: Long
) = object: BundleProperty<Long>(name) {

    override fun getValue(bundle: Bundle, name: String): Long = bundle.getLong(name)

    override fun setValue(bundle: Bundle, name: String, value: Long) = bundle.putLong(name, value)

    override fun getUnexistsValue(): Long = default

}

fun bundleLong(
        name: String? = null
) = object : OptionalBundleProperty<Long>(name) {

    override fun getValue(bundle: Bundle, name: String): Long? = bundle.getLong(name)

    override fun setValue(bundle: Bundle, name: String, value: Long) = bundle.putLong(name, value)

}

fun bundleFloat(
    name: String? = null,
    default: Float
) = object: BundleProperty<Float>(name) {

    override fun getValue(bundle: Bundle, name: String): Float = bundle.getFloat(name)

    override fun setValue(bundle: Bundle, name: String, value: Float) = bundle.putFloat(name, value)

    override fun getUnexistsValue(): Float = default

}

fun bundleFloat(
    name: String? = null
) = object : OptionalBundleProperty<Float>(name) {

    override fun getValue(bundle: Bundle, name: String): Float? = bundle.getFloat(name)

    override fun setValue(bundle: Bundle, name: String, value: Float) = bundle.putFloat(name, value)

}

fun bundleDouble(
    name: String? = null,
    default: Double
) = object: BundleProperty<Double>(name) {

    override fun getValue(bundle: Bundle, name: String): Double = bundle.getDouble(name)

    override fun setValue(bundle: Bundle, name: String, value: Double) = bundle.putDouble(name, value)

    override fun getUnexistsValue(): Double = default

}

fun bundleDouble(
    name: String? = null
) = object : OptionalBundleProperty<Double>(name) {

    override fun getValue(bundle: Bundle, name: String): Double? = bundle.getDouble(name)

    override fun setValue(bundle: Bundle, name: String, value: Double) = bundle.putDouble(name, value)

}

fun bundleString(
    name: String? = null,
    default: String
) = object: BundleProperty<String>(name) {

    override fun getValue(bundle: Bundle, name: String): String = bundle.getString(name, default)

    override fun setValue(bundle: Bundle, name: String, value: String) = bundle.putString(name, value)

    override fun getUnexistsValue(): String = default

}

fun bundleString(
    name: String? = null
) = object : OptionalBundleProperty<String>(name) {

    override fun getValue(bundle: Bundle, name: String): String? = bundle.getString(name)

    override fun setValue(bundle: Bundle, name: String, value: String) = bundle.putString(name, value)

}

fun <T: Parcelable> bundleParcelable(
    name: String? = null,
    default: T
) = object: BundleProperty<T>(name) {

    override fun getValue(bundle: Bundle, name: String): T = bundle.getParcelable(name)!!

    override fun setValue(bundle: Bundle, name: String, value: T) = bundle.putParcelable(name, value)

    override fun getUnexistsValue(): T = default

}

fun <T: Parcelable> bundleParcelable(
    name: String? = null
) = object : OptionalBundleProperty<T>(name) {

    override fun getValue(bundle: Bundle, name: String): T? = bundle.getParcelable(name)

    override fun setValue(bundle: Bundle, name: String, value: T) = bundle.putParcelable(name, value)

}

abstract class BundleProperty<T: Any>(
        private val name: String?
): ReadWriteProperty<Bundle, T> {

    override fun getValue(thisRef: Bundle, property: KProperty<*>): T {

        val key = key(property)

        return if (thisRef.containsKey(key)) getValue(thisRef, key)
               else getUnexistsValue()

    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T) {
        setValue(thisRef, key(property), value)
    }

    protected abstract fun getValue(bundle: Bundle, name: String): T

    protected abstract fun setValue(bundle: Bundle, name: String, value: T)

    protected abstract fun getUnexistsValue(): T

    private fun key(property: KProperty<*>): String = name ?: property.name

}

abstract class OptionalBundleProperty<T: Any>(
    private val name: String?
): ReadWriteProperty<Bundle, T?> {

    override fun getValue(thisRef: Bundle, property: KProperty<*>): T? {

        val key = key(property)

        return if (thisRef.containsKey(key)) getValue(thisRef, key)
        else null

    }

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: T?) {

        val key = key(property)

        if (value == null) {
            thisRef.remove(key)
        } else {
            setValue(thisRef, key, value)
        }

    }

    protected abstract fun getValue(bundle: Bundle, name: String): T?

    protected abstract fun setValue(bundle: Bundle, name: String, value: T)

    private fun key(property: KProperty<*>): String = name ?: property.name

}