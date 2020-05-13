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
    Cleareable {

    private val propertyChangeRegistry = PropertyChangeRegistry()

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) =
        propertyChangeRegistry.remove(callback)

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) =
        propertyChangeRegistry.add(callback)

    override fun notifyChange() = propertyChangeRegistry.notifyChange(this, BR._all)

    override fun notifyPropertyChanged(propertyId: Int) =
        propertyChangeRegistry.notifyChange(this, propertyId)

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        clear()
    }

    override fun clear() { }

}