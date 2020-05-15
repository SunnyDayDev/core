package dev.sunnyday.core.mvvm.observable

import androidx.databinding.Observable
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import dev.sunnyday.core.propertydelegate.weak
import dev.sunnyday.core.ui.lifecycle.addScopeObserver
import dev.sunnyday.core.util.Weak
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

inline fun <reified T: Observable, P> T.addOnPropertyChangedCallback(
    property: KProperty0<P>,
    noinline callback: (P) -> Unit
): ExtendedOnPropertyChangedCallback<T> {
    val id = Bindables.getBindablePropertyId(property)

    return addOnPropertyChangedCallback(id) { _, propertyId ->
        if (propertyId == id) {
            callback(property.get())
        }
    }
}

inline fun <reified T: Observable> T.addOnPropertyChangedCallback(
    properties: List<KProperty<*>>,
    noinline callback: (T) -> Unit
): ExtendedOnPropertyChangedCallback<T> {
    val ids: IntArray = properties
        .map { it.getBindablePropertyId() }
        .toIntArray()

    return addOnPropertyChangedCallback(
        propertiesIds = *ids
    ) { sender, _ ->
        callback(sender)
    }
}

inline fun <reified T: Observable> T.addOnPropertyChangedCallback(
    vararg propertiesIds: Int,
    noinline callback: (T, Int) -> Unit
): ExtendedOnPropertyChangedCallback<T> {
    val observableCallback =
        LambdaOnPropertyChangedCallback(this) { sender, propertyId ->
            if (
                propertyId == BR._all ||
                propertiesIds.isEmpty() ||
                propertiesIds.contains(propertyId)
            ) {
                callback(sender, propertyId)
            }
        }

    addOnPropertyChangedCallback(observableCallback)
    
    return observableCallback
}

fun <T: Observable> ExtendedOnPropertyChangedCallback<T>.attachToLifecycle(
    lifeCycleOwner: LifecycleOwner,
    scope: Lifecycle.State = Lifecycle.State.CREATED
) = attachToLifecycle(lifeCycleOwner.lifecycle, scope)

fun <T: Observable> ExtendedOnPropertyChangedCallback<T>.attachToLifecycle(
    lifeCycle: Lifecycle,
    scope: Lifecycle.State = Lifecycle.State.CREATED
) {

    if (!lifeCycle.currentState.isAtLeast(scope)) {
        this.owner?.removeOnPropertyChangedCallback(this)
    }

    lifeCycle.addScopeObserver(
        scope,
        onEnter = { owner?.addOnPropertyChangedCallback(this) },
        onExit = { owner?.removeOnPropertyChangedCallback(this) }
    )
}

abstract class ExtendedOnPropertyChangedCallback<T: Observable>(
    owner: T
): Observable.OnPropertyChangedCallback() {

    internal val owner by weak(owner)

    final override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        val owner = this.owner ?: return
        if (owner !== sender) return
        onCheckedOnPropertyChanged(owner, propertyId)
    }

    abstract fun onCheckedOnPropertyChanged(sender: T, propertyId: Int)

}

class LambdaOnPropertyChangedCallback<T: Observable>(
    owner: T,
    private val callback: (T, Int) -> Unit
) : ExtendedOnPropertyChangedCallback<T>(owner) {

    override fun onCheckedOnPropertyChanged(sender: T, propertyId: Int) =
        callback(sender, propertyId)

}