package dev.sunnyday.core.mvvm

import androidx.lifecycle.ViewModelProvider
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.lifecycle.ViewModel
import dev.sunnyday.core.ui.activity.CoreActivity
import dev.sunnyday.core.ui.listener.OnBackPressedListener

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */


abstract class MVVMActivity<Binding: ViewDataBinding, VM: ViewModel>: CoreActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): VM

    // endregion

    protected lateinit var viewModel: VM
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        val injected = proceedInjectionBeforeOnCreate()
        super.onCreate(savedInstanceState)
        if (!injected) proceedInjectionAtOnCreate()
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        val viewModel = getViewModel(ViewModelProvider(this, viewModelFactory))
            .also { this.viewModel = it }

        binding.setVariable(viewModelVariableId, viewModel)
        if (viewModel is OnBackPressedListener) {
            onBackPressedRegistry.add(viewModel)
        }

        onViewModelCreated(viewModel)
    }

    protected fun onViewModelCreated(viewModel: VM) {
        // no-op
    }

    protected open fun proceedInjectionBeforeOnCreate(): Boolean = false

    protected open fun proceedInjectionAtOnCreate() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()

        if (::viewModel.isInitialized) {
            val viewModel = this.viewModel
            if (viewModel is OnBackPressedListener) {
                onBackPressedRegistry.remove(viewModel)
            }
        }
    }

}