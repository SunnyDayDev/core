package dev.sunnyday.core.mvvm.viewModel

import androidx.databinding.Observable
import dev.sunnyday.core.mvvm.observable.Bindables
import dev.sunnyday.core.mvvm.observable.NotifiableObservable
import dev.sunnyday.core.util.Weak
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0


/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMViewModelProjection(private val root: NotifiableObservable): MVVMViewModel() {

    private val propertyMap = mutableMapOf<Int, MutableSet<Int>>()

    private val renotifyCallback by lazy {
        val weak = Weak<Any>(this)

        object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (weak.value == null) {
                    root.removeOnPropertyChangedCallback(this)
                } else {
                    val mappedPropertyIds = propertyMap[propertyId] ?: return
                    mappedPropertyIds.forEach {
                        notifyPropertyChanged(it)
                    }
                }
            }
        }
    }

    init {
        root.addOnPropertyChangedCallback(renotifyCallback)
    }

    protected fun <T> rebindable(
        delegatedProperty: KProperty0<T>
    ): RebindableReadOnlyProperty<T> =
        RebindableReadOnlyProperty(delegatedProperty)

    protected fun <T> rebindable(
        delegatedProperty: KMutableProperty0<T>
    ): RebindableReadWriteProperty<T> =
        RebindableReadWriteProperty(delegatedProperty)

    protected open inner class RebindableReadOnlyProperty<T>(
        private val delegatedProperty: KProperty0<T>
    ) : ReadOnlyProperty<NotifiableObservable, T> {

        private val delegatedPropertyId = Bindables.getBindablePropertyId(delegatedProperty)

        private var isRegistered = false

        override fun getValue(thisRef: NotifiableObservable, property: KProperty<*>): T {
            registerRebind(property)
            return delegatedProperty.get()
        }

        open fun registerRebind(property: KProperty<*>) = apply {
            if (isRegistered) return this
            isRegistered = true

            val propertyId = Bindables.getBindablePropertyId(property)

            propertyMap.getOrPut(delegatedPropertyId, ::mutableSetOf)
                .add(propertyId)
        }

    }

    protected inner class RebindableReadWriteProperty<T>(
        private val delegatedProperty: KMutableProperty0<T>
    ) : RebindableReadOnlyProperty<T>(delegatedProperty),
        ReadWriteProperty<NotifiableObservable, T> {

        override fun setValue(thisRef: NotifiableObservable, property: KProperty<*>, value: T) {
            registerRebind(property)
            delegatedProperty.set(value)
        }

        override fun registerRebind(property: KProperty<*>) = apply {
            super.registerRebind(property)
        }

    }

}