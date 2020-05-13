package dev.sunnyday.core.mvvm.viewModel

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import dev.sunnyday.core.mvvm.observable.NotifiableBaseObservable
import dev.sunnyday.core.mvvm.observable.NotifiableObservable

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMViewModel :
    ViewModel(),
    NotifiableObservable by NotifiableBaseObservable(),
    Cleareable {

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        clear()
    }

    override fun clear() { }

}