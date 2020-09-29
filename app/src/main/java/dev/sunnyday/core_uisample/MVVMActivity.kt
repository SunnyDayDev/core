package dev.sunnyday.core_uisample

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.ViewModelProvider
import dev.sunnyday.core.mvvm.observable.addOnPropertyChangedCallback
import dev.sunnyday.core.mvvm.observable.bindable
import dev.sunnyday.core.mvvm.observable.getBindablePropertyId
import dev.sunnyday.core.ui.source.Source
import dev.sunnyday.core.ui.source.DrawableSource
import dev.sunnyday.core.mvvm.util.setContentBinding
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel
import dev.sunnyday.core.mvvm.viewModel.get
import dev.sunnyday.core.ui.source.TextSource
import dev.sunnyday.core.util.AppGlobals
import dev.sunnyday.core_uisample.databinding.ActivityMvvmBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import java.util.concurrent.TimeUnit

class MVVMActivity : dev.sunnyday.core.mvvm.MVVMActivity<ActivityMvvmBinding, MVVMViewModel>() {

    override val viewModelFactory: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()
    override val viewModelVariableId: Int = BR.vm
    override val binding: ActivityMvvmBinding by lazy { setContentBinding(R.layout.activity_mvvm) }
    override fun getViewModel(provider: ViewModelProvider): MVVMViewModel = provider[ViewModel::class]

    class ViewModel: MVVMViewModel() {

        val drawable: Source<Drawable> = DrawableSource.Uri(Uri.parse("android.resource://${AppGlobals.applicationContext.packageName}/drawable/ic_shape"))
        val drawable2: Source<Drawable> = DrawableSource.Uri(Uri.parse("android.resource://${AppGlobals.applicationContext.packageName}/${R.drawable.ic_shape}"))
        val drawable3: Source<Drawable> = DrawableSource.Uri(Uri.parse("https://upload.wikimedia.org/wikipedia/commons/4/47/PNG_transparency_demonstration_1.png"))

        @get:Bindable
        var text: Source<CharSequence> by bindable(TextSource("Text source test"))
            private set

        @get:Bindable
        var tick: String by bindable("0")
            private set

        @get:Bindable
        var tickColor: Int by bindable(Color.BLACK)

        fun onFocusChange(hasFocus: Boolean) {
            tickColor =
                if (hasFocus) Color.RED
                else Color.BLACK
        }

        private val dispose = CompositeDisposable()

        init {
            Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    tick = "$it"
                }
                .addTo(dispose)

            addOnPropertyChangedCallback(this::tick) {
                text = TextSource("Tick property id: ${this::tick.getBindablePropertyId()}")
            }
        }

        override fun clear() {
            super.clear()
            dispose.clear()
        }

    }

}
