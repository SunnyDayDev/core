package dev.sunnyday.core.mvvm

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMBottomSheetDialogFragment<Binding: ViewDataBinding, VM: ViewModel>: BottomSheetDialogFragment()  {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract fun onCreateBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           savedInstanceState: Bundle?): Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): VM

    // endregion

    protected lateinit var viewModel: VM
        private set

    protected var binding: Binding? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        proceedInjection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        viewModel = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        onViewModelCreated(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            onCreateBinding(inflater, container, savedInstanceState)
                    .apply {
                        setVariable(viewModelVariableId, viewModel)
                    }
                    .also { binding = it }
                    .root

    protected open fun onViewModelCreated(viewModel: VM) {
        // no-op
    }

    protected open fun proceedInjection() {
        // no-op
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
        binding = null
    }

}