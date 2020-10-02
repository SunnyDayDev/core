package dev.sunnyday.core.mvvm.viewModel

import androidx.annotation.CallSuper
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModel
import dev.sunnyday.core.mvvm.observable.NotifiableObservable

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMViewModel :
    ViewModel(),
    NotifiableObservable,
    Cleanable {

    private val propertyChangeRegistry = PropertyChangeRegistry()

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.remove(callback)
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.add(callback)
    }

    override fun notifyChange() {
        propertyChangeRegistry.notifyCallbacks(this, BR._all, null)
    }

    override fun notifyPropertyChanged(propertyId: Int) {
        propertyChangeRegistry.notifyCallbacks(this, propertyId, null)
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        clean()
    }

    override fun clean() { }

}