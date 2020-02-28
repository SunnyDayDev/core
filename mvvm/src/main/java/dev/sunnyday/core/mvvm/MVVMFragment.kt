package dev.sunnyday.core.mvvm

import android.content.Context
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import dev.sunnyday.core.ui.fragment.CoreFragment

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMFragment<Binding: ViewDataBinding, VM: ViewModel>: CoreFragment() {

    // region Abstract

    protected abstract val viewModelVariableId: Int

    protected abstract val viewModel: VM

    protected abstract fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Binding

    // endregion

    private var isNotInjected: Boolean = true

    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: Binding? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (isNotInjected) {
            isNotInjected = false
            proceedInjection()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onViewModelCreated(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        onCreateBinding(inflater, container, savedInstanceState)
            .apply {
                onBindViewModel()
                setVariable(viewModelVariableId, viewModel)
            }
            .also { binding = it }
            .root

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