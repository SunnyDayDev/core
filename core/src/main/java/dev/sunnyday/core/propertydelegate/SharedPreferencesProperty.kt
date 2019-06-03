package dev.sunnyday.core.propertydelegate

import android.annotation.SuppressLint
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 16.08.2018.
 * mail: mail@sunnyday.dev
 */

fun sharedPreferencesBoolean(
    name: String? = null,
    default: Boolean
) = object: SharedPreferencesProperty<Boolean>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Boolean = prefs.getBoolean(name, default)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Boolean) = prefs.putBoolean(name, value)

    override fun getUnexistsValue(): Boolean = default

}

fun sharedPreferencesBoolean(
    name: String? = null
) = object : OptionalSharedPreferencesProperty<Boolean>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Boolean? = prefs.getBoolean(name, false)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Boolean) = prefs.putBoolean(name, value)

}

fun sharedPreferencesInt(
    name: String? = null,
    default: Int
) = object: SharedPreferencesProperty<Int>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Int = prefs.getInt(name, default)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Int) = prefs.putInt(name, value)

    override fun getUnexistsValue(): Int = default

}

fun sharedPreferencesInt(
    name: String? = null
) = object: OptionalSharedPreferencesProperty<Int>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Int = prefs.getInt(name, 0)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Int) = prefs.putInt(name, value)

}

fun sharedPreferencesLong(
    name: String? = null,
    default: Long
) = object: SharedPreferencesProperty<Long>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Long = prefs.getLong(name, default)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Long) = prefs.putLong(name, value)

    override fun getUnexistsValue(): Long = default

}

fun sharedPreferencesLong(
        name: String? = null
) = object : OptionalSharedPreferencesProperty<Long>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Long? = prefs.getLong(name, 0)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Long) = prefs.putLong(name, value)

}

fun sharedPreferencesFloat(
    name: String? = null,
    default: Float
) = object: SharedPreferencesProperty<Float>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Float = prefs.getFloat(name, default)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Float) = prefs.putFloat(name, value)

    override fun getUnexistsValue(): Float = default

}

fun sharedPreferencesFloat(
    name: String? = null
) = object : OptionalSharedPreferencesProperty<Float>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Float? = prefs.getFloat(name, 0.0f)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Float) = prefs.putFloat(name, value)

}

fun sharedPreferencesString(
    name: String? = null,
    default: String
) = object: SharedPreferencesProperty<String>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): String = prefs.getString(name, default)!!

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: String) = prefs.putString(name, value)

    override fun getUnexistsValue(): String = default

}

fun sharedPreferencesString(
    name: String? = null
) = object : OptionalSharedPreferencesProperty<String>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): String? = prefs.getString(name, null)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: String) = prefs.putString(name, value)

}

fun sharedPreferencesStringSet(
    name: String? = null,
    default: Set<String>
) = object: SharedPreferencesProperty<Set<String>>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Set<String> = prefs.getStringSet(name, default)!!

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Set<String>) = prefs.putStringSet(name, value)

    override fun getUnexistsValue(): Set<String> = default

}

fun sharedPreferencesStringSet(
    name: String? = null
) = object : OptionalSharedPreferencesProperty<Set<String>>(name) {

    override fun getValue(prefs: SharedPreferences, name: String): Set<String>? = prefs.getStringSet(name, null)

    override fun setValue(prefs: SharedPreferences.Editor, name: String, value: Set<String>) = prefs.putStringSet(name, value)

}

abstract class SharedPreferencesProperty<T: Any>(
    private val name: String?
): ReadWriteProperty<SharedPreferences, T> {

    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): T {

        val key = key(property)

        return if (thisRef.contains(key)) getValue(thisRef, key)
        else getUnexistsValue()

    }

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: T) {
        setValue(thisRef.edit(), key(property), value).apply()
    }

    protected abstract fun getValue(prefs: SharedPreferences, name: String): T

    protected abstract fun setValue(prefs: SharedPreferences.Editor, name: String, value: T): SharedPreferences.Editor

    protected abstract fun getUnexistsValue(): T

    private fun key(property: KProperty<*>): String = name ?: property.name

}

abstract class OptionalSharedPreferencesProperty<T: Any>(
    private val name: String?
): ReadWriteProperty<SharedPreferences, T?> {

    override fun getValue(thisRef: SharedPreferences, property: KProperty<*>): T? {

        val key = key(property)

        return if (thisRef.contains(key)) getValue(thisRef, key)
        else null

    }

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: SharedPreferences, property: KProperty<*>, value: T?) {

        val key = key(property)

        if (value == null) {
            thisRef.edit().remove(key).apply()
        } else {
            setValue(thisRef.edit(), key, value).apply()
        }

    }

    protected abstract fun getValue(prefs: SharedPreferences, name: String): T?

    protected abstract fun setValue(prefs: SharedPreferences.Editor, name: String, value: T): SharedPreferences.Editor

    private fun key(property: KProperty<*>): String = name ?: property.name

}
