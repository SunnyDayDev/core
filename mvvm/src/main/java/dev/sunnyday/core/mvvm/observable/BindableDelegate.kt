package dev.sunnyday.core.mvvm.observable

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import timber.log.Timber

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnyday.dev
 */

object Bindables {

    private val reflectFields by lazy {
        val clazz = try {
            Class.forName("androidx.databinding.library.baseAdapters.BR")
        } catch (e: Throwable) {
            Timber.e(e)
            null
        } ?: return@lazy emptyMap<String, Int>()

        scanFieldsMap(clazz)
    }

    private var manualFields: Map<String, Int>? = null

    var fieldsMap: Map<String, Int>
        get() = manualFields ?: reflectFields
        set(value) {
            manualFields = value
        }

    private fun scanFieldsMap(clazz: Class<*>): Map<String, Int> = clazz.fields
        .filter { it.type == Int::class.javaObjectType || it.type == Int::class.javaPrimitiveType }
        .associate { it.name to it.get(null) as Int }

    fun getBindablePropertyId(property: KProperty<*>): Int {
        val fieldsMap = this.fieldsMap

        return fieldsMap[property.name]
            ?: fieldsMap[property.fallbackName]
            ?: throw IllegalStateException("Unknown bindable property: $property")
    }

    private val KProperty<*>.fallbackName: String
        get() {
            if (!name.startsWith("is")) return name
            return name[2].toLowerCase() + name.substring(3)
        }

}

internal class BindableDelegate<in R : Notifiable, T : Any?>(
    private var value: T,
    private val id: Int?,
    private var notifyOnlyIfChanged: Boolean,
    private val onChange: ((T) -> Unit)? = null
) : ReadWriteProperty<R, T> {

    private var cachedCheckedId: Int? = null

    override operator fun getValue(thisRef: R, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: R, property: KProperty<*>, value: T) {
        val shouldNotify = !notifyOnlyIfChanged || this.value != value
        this.value = value

        if (shouldNotify) {
            notifyChanged(thisRef, property, value)
        }
    }

    private fun notifyChanged(thisRef: R, property: KProperty<*>, value: T) {
        val checkedId = getBindablePropertyId(property)

        thisRef.notifyPropertyChanged(checkedId)
        onChange?.invoke(value)
    }

    private fun getBindablePropertyId(property: KProperty<*>): Int =
        id ?: cachedCheckedId ?: resolveBindablePropertyId(property)

    private fun resolveBindablePropertyId(property: KProperty<*>): Int =
        Bindables.getBindablePropertyId(property)
            .also(this::cachedCheckedId::set)

}

fun <R : Notifiable, T : Any?> bindable(
    initialValue: T,
    id: Int? = null,
    notifyOnlyIfChanged: Boolean = true,
    onChange: ((T) -> Unit)? = null
): ReadWriteProperty<R, T> = BindableDelegate(initialValue, id, notifyOnlyIfChanged, onChange)