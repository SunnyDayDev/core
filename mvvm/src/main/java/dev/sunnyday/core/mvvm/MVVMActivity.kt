package dev.sunnyday.core.mvvm

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import dev.sunnyday.core.util.Late
import dev.sunnyday.core.util.lateinit
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel
import dev.sunnyday.core.ui.activity.CoreActivity
import dev.sunnyday.core.ui.listener.OnBackPressedListener

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */


abstract class MVVMActivity<Binding: ViewDataBinding>: CoreActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    private val lateinitViewModelValue = Late<MVVMViewModel>()
    protected val viewModel: MVVMViewModel by lateinit(lateinitViewModelValue)

    override fun onCreate(savedInstanceState: Bundle?) {
        val injected = proceedInjectionBeforeOnCreate()
        super.onCreate(savedInstanceState)
        if (!injected) proceedInjectionAtOnCreate()
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        val viewModelValue = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        lateinitViewModelValue.set(viewModelValue)

        with(viewModel) {

            binding.setVariable(viewModelVariableId, this)
            if (this is OnBackPressedListener) {
                onBackPressedRegistry.add(this)
            }

            onViewModelCreated(viewModel)

        }

    }

    private fun onViewModelCreated(viewModel: MVVMViewModel) {
        // no-op
    }

    protected open fun proceedInjectionBeforeOnCreate(): Boolean = false

    protected open fun proceedInjectionAtOnCreate() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()

        with(viewModel) {
            if (this is OnBackPressedListener) {
                onBackPressedRegistry.remove(this)
            }
        }

    }

}