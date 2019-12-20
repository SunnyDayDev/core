package dev.sunnyday.core_uisample

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import dev.sunnyday.core.ui.source.Source
import dev.sunnyday.core.ui.source.DrawableSource
import dev.sunnyday.core.mvvm.util.setContentBinding
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel
import dev.sunnyday.core.mvvm.viewModel.get
import dev.sunnyday.core.util.AppGlobals
import dev.sunnyday.core_uisample.databinding.ActivityMvvmBinding

class MVVMActivity : dev.sunnyday.core.mvvm.MVVMActivity<ActivityMvvmBinding, MVVMViewModel>() {

    override val viewModelFactory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
    override val viewModelVariableId: Int = BR.vm
    override val binding: ActivityMvvmBinding by lazy { setContentBinding<ActivityMvvmBinding>(R.layout.activity_mvvm) }
    override fun getViewModel(provider: ViewModelProvider): MVVMViewModel = provider[ViewModel::class]

    class ViewModel: MVVMViewModel() {

        val drawable: Source<Drawable> = DrawableSource.Uri(Uri.parse("android.resource://${AppGlobals.applicationContext.packageName}/drawable/ic_shape"))
        val drawable2: Source<Drawable> = DrawableSource.Uri(Uri.parse("android.resource://${AppGlobals.applicationContext.packageName}/${R.drawable.ic_shape}"))
        val drawable3: Source<Drawable> = DrawableSource.Uri(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png"))

    }

}
