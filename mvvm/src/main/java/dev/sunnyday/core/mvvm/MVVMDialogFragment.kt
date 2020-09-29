package dev.sunnyday.core.mvvm

import androidx.lifecycle.ViewModelProvider
import android.content.Context
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import dev.sunnyday.core.ui.fragment.CoreDialogFragment

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMDialogFragment<Binding: ViewDataBinding, VM: ViewModel>: CoreDialogFragment() {

    // region Abstract

    protected abstract val viewModelVariableId: Int

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract fun onCreateBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           savedInstanceState: Bundle?): Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): VM

    // endregion

    protected lateinit var viewModel: VM
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: Binding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        proceedInjection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(ViewModelProvider(this, viewModelFactory))
        onViewModelCreated(viewModel)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return onCreateBinding(inflater, container, savedInstanceState)
                .apply {
                    onBindViewModel()
                    setVariable(viewModelVariableId, viewModel)
                }
                .also { binding = it }
                .root

    }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.unbind()
        binding = null
        onUnbindViewModel()

    }

    protected open fun onViewModelCreated(viewModel: VM) {
        // no-op
    }

    protected fun onBindViewModel() {
        // no-op
    }

    protected fun onUnbindViewModel() {
        // no-op
    }

    protected open fun proceedInjection() {
        // no-op
    }

}