package dev.sunnyday.core.mvvm

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.sunnyday.core.util.Late
import dev.sunnyday.core.util.lateinit
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnyday.dev
 */

abstract class MVVMBottomSheetDialog<Binding: ViewDataBinding>: BottomSheetDialog  {

    protected val activity: FragmentActivity

    constructor(activity: FragmentActivity): super(activity) {
        this.activity = activity
    }

    constructor(activity: FragmentActivity, theme: Int): super(activity, theme){
        this.activity = activity
    }

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
        val viewModelValue = getViewModel(ViewModelProviders.of(activity, viewModelFactory))
        lateinitViewModelValue.set(viewModelValue)

        with(viewModel) {

            binding.setVariable(viewModelVariableId, this)

            onViewModelCreated(viewModel)

        }

    }

    protected inline fun <reified T: ViewDataBinding>setContentBinding(layoutId: Int): T {
        val binding: T = DataBindingUtil.inflate(
                activity.layoutInflater, layoutId, null, false)
        setContentView(binding.root)
        return binding
    }

    private fun onViewModelCreated(viewModel: MVVMViewModel) {
        // no-op
    }

    protected open fun proceedInjectionBeforeOnCreate(): Boolean = false

    protected open fun proceedInjectionAtOnCreate() {
        // no-op
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        binding.unbind()
    }

}