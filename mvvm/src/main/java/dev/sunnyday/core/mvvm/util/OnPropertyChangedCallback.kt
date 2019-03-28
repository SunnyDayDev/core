package dev.sunnyday.core.mvvm.util

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import dev.sunnyday.core.ui.lifecycle.addScopeObserver
import dev.sunnyday.core.util.Weak

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 03.08.2018.
 * mail: mail@sunnyday.dev
 */

private class OnPropertyChangedCallback<T: Observable>(
        private val action: (T, Int) -> Unit
): Observable.OnPropertyChangedCallback() {

    override fun onPropertyChanged(sender: Observable, propertyId: Int) {
        @Suppress("UNCHECKED_CAST")
        val target = sender as? T ?: return
        action(target, propertyId)
    }

}

fun <T: Observable> T.addOnPropertyChangedCallback(
        action: (T, Int) -> Unit
): Observable.OnPropertyChangedCallback {
    val callback = OnPropertyChangedCallback(action)
    addOnPropertyChangedCallback(callback)
    return callback
}

fun <T: Observable> T.addOnPropertyChangedCallback(
    lifeCycle: Lifecycle,
    scope: Lifecycle.State = Lifecycle.State.CREATED,
    listener: (T, Int) -> Unit
) {

    val weakCallback = Weak<Observable.OnPropertyChangedCallback>()

    lifeCycle.addScopeObserver(
        scope,
        onEnter = { weakCallback.value = addOnPropertyChangedCallback(listener)  },
        onExit = { weakCallback.value?.let(::removeOnPropertyChangedCallback) }
    )

}