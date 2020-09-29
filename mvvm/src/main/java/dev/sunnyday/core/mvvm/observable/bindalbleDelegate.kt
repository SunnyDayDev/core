package dev.sunnyday.core.mvvm.observable

import androidx.databinding.Observable
import dev.sunnyday.core.util.Late
import dev.sunnyday.core.util.Weak
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

fun <O: NotifiableObservable, T> O.bindableGetDelegate(
    property: (O) -> KProperty0<T>
): ReadOnlyProperty<NotifiableObservable, T> =
    ReadOnlyBindablePropertyDelegate(this, property(this))

fun <O: NotifiableObservable, T> O.bindableDelegate(
    property: (O) -> KMutableProperty0<T>
): ReadWriteProperty<NotifiableObservable, T> =
    ReadWriteBindablePropertyDelegate(this, property(this))

private open class ReadOnlyBindablePropertyDelegate<T>(
    private val originalPropertyOwner: NotifiableObservable,
    private val originalProperty: KProperty0<T>
): ReadOnlyProperty<NotifiableObservable, T> {

    private var isInitialized = false

    override fun getValue(thisRef: NotifiableObservable, property: KProperty<*>): T {
        if (!isInitialized) {
            initialize(thisRef, property)
        }

        return originalProperty.get()
    }

    private fun initialize(thisRef: NotifiableObservable, property: KProperty<*>) {
        val callback: Late<Observable.OnPropertyChangedCallback> = Late()
        val weakThisRef = Weak(thisRef)

        originalPropertyOwner
            .addOnPropertyChangedCallback(originalProperty) {
                val currentThisRef = weakThisRef.value

                if (currentThisRef == null) {
                    originalPropertyOwner.removeOnPropertyChangedCallback(callback.value)
                } else {
                    currentThisRef.notifyPropertyChanged(Bindables.getBindablePropertyId(property))
                }
            }
            .also(callback::value::set)
    }

}

private class ReadWriteBindablePropertyDelegate<T>(
    originalPropertyOwner: NotifiableObservable,
    private val originalProperty: KMutableProperty0<T>
): ReadOnlyBindablePropertyDelegate<T>(originalPropertyOwner, originalProperty),
    ReadWriteProperty<NotifiableObservable, T> {

    override fun setValue(thisRef: NotifiableObservable, property: KProperty<*>, value: T) =
        originalProperty.set(value)

}