package dev.sunnyday.core.mvvm.binding

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingConversion
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import dev.sunnyday.core.mvvm.R
import dev.sunnyday.core.ui.util.findActivity
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.net.URL


/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnyday.dev
 */

object ImageViewBindings: Bindings() {

    @JvmStatic
    @BindingConversion
    fun convertStringToUri(string: String?): Uri? = string?.let { Uri.parse(it) }

    @JvmStatic
    @BindingConversion
    fun convertURLToUri(string: URL?): Uri? = string?.let { Uri.parse(it.toString()) }

    @JvmStatic
    @BindingAdapter("imageSource")
    fun bindImageDrawable(view: ImageView, source: ImageSource?) = view.core.setSource(source)

    @JvmStatic
    @BindingAdapter("imageDrawable")
    fun bindImageDrawable(view: ImageView, drawable: Drawable?) =
            view.core.setDrawable(drawable)

    @JvmStatic
    @BindingAdapter("imageUri")
    fun bindImageUri(view: ImageView, uri: Uri?) = view.core.setUri(uri)

    @JvmStatic
    @BindingAdapter("imageUriCenterCrop")
    fun bindImageUriCenterCrop(view: ImageView, uri: Boolean?) =
            view.core.setUriCenterCrop(uri)

    @JvmStatic
    @BindingAdapter("imageUriCenterInside")
    fun bindImageUriCenterInside(view: ImageView, uri: Boolean?) =
            view.core.setUriCenterInside(uri)

    @JvmStatic
    @BindingAdapter("imageUriCircleCrop")
    fun bindImageUriCircleCrop(view: ImageView, uri: Boolean?) =
            view.core.setUriCircleCrop(uri)

    @JvmStatic
    @BindingAdapter("imageUriFitCenter")
    fun bindImageUriFitCenter(view: ImageView, uri: Boolean?) =
            view.core.setUriFitCenter(uri)

    @JvmStatic
    @BindingAdapter("imageUriTransformation")
    fun bindImageUriTransformation(view: ImageView, transformation: Transformation<Bitmap>?) =
            view.core.setUriTransformation(transformation)

    @JvmStatic
    @BindingAdapter("imageUriOptions")
    fun bindImageUriOptions(view: ImageView, options: RequestOptions?) =
            view.core.setUriOptions(options)

    @JvmStatic
    @BindingAdapter("srcCompat")
    fun bindSrcCompat(view: ImageView, @DrawableRes id: Int) = view.core.setSrcCompat(id)

    @JvmStatic
    @BindingAdapter("src")
    fun bindSrc(view: ImageView, @DrawableRes id: Int) = view.core.setSrc(id)

    private val ImageView.core get() =
        getOrSetListener(R.id.binding_imageview_source_core) { SourceCore(this) }

    private class SourceCore(view: ImageView): BindableCore<ImageView, BindableCore.Change.Simple>(view) {

        private val uriConfig by lazy { UriConfig() }

        private var applier: ((ImageView) -> Unit)? = null

        private var uriMode = false

        fun setSource(source: ImageSource?) = when(source) {
            is ImageSource.Uri -> setUri(source.uri)
            is ImageSource.Drawable -> setDrawable(source.drawable)
            is ImageSource.Resource -> setSrcCompat(source.id)
            null -> {
                applier = { view.setImageDrawable(null) }
                notifyChanges()
            }
        }

        fun setDrawable(drawable: Drawable?) {
            applier = {
                view.setImageDrawable(drawable)
            }
            notifyChanges()
        }

        fun setUri(uri: Uri?) {
            uriMode = true
            uriConfig.uri = uri
            applier = { applyUri(uriConfig) }
            notifyChanges()
        }

        fun setUriCenterCrop(use: Boolean?) {
            uriConfig.centerCrop = use
            onUriConfigChanged()
        }

        fun setUriCenterInside(use: Boolean?) {
            uriConfig.centerInside = use
            onUriConfigChanged()
        }

        fun setUriCircleCrop(use: Boolean?) {
            uriConfig.circleCrop = use
            onUriConfigChanged()
        }

        fun setUriFitCenter(use: Boolean?) {
            uriConfig.fitCenter = use
            onUriConfigChanged()
        }

        fun setUriTransformation(transformation: Transformation<Bitmap>?) {
            uriConfig.transformation = transformation
            onUriConfigChanged()
        }

        fun setUriOptions(options: RequestOptions?) {
            uriConfig.options = options
            onUriConfigChanged()
        }

        fun setSrc(@DrawableRes id: Int) {
            uriMode = false
            applier = {
                if (id == -1) {
                    view.setImageDrawable(null)
                } else {
                    view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
                }
            }
            notifyChanges()
        }

        fun setSrcCompat(@DrawableRes id: Int) {
            uriMode = false
            applier = {
                if (id == -1) {
                    view.setImageDrawable(null)
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
                    } else {
                        try {
                            view.setImageDrawable(ContextCompat.getDrawable(view.context, id))
                        } catch (e: Throwable) {
                            try {
                                val vector = VectorDrawableCompat.create(
                                        view.resources, id, view.findActivity()?.theme)
                                view.setImageDrawable(vector)
                            } catch (ignored: Throwable) {
                                throw e
                            }
                        }
                    }
                }
            }
            notifyChanges()
        }

        override fun applyChanges(changes: List<Change.Simple>) {
            applier?.invoke(view)
        }

        private fun onUriConfigChanged() {
            if (uriMode) {
                applier = { applyUri(uriConfig) }
                notifyChanges()
            }
        }

        private fun applyUri(uriConfig: UriConfig) {
            try {

                val glide = Glide.with(view)

                glide.clear(view)

                val uri = uriConfig.uri ?: return Unit.also {
                    view.setImageDrawable(null)
                }

                val options = (uriConfig.options ?: RequestOptions())
                        .applyIf(uriConfig.centerCrop) { centerCrop() }
                        .applyIf(uriConfig.centerInside) { centerInside() }
                        .applyIf(uriConfig.circleCrop) { circleCrop() }
                        .applyIf(uriConfig.fitCenter) { fitCenter() }
                        .applyIf(uriConfig.transformation) { transform(it) }

                glide.load(uri)
                        .apply(options)
                        .into(view)

            } catch (e: Throwable) {

                if (e is IllegalArgumentException &&
                        e.stackTrace.first().methodName == "assertNotDestroyed") {
                    // Can't load to destroyed activity
                    // but it's not matter because it will rebindined on new onCreate.
                    // Do nothing.
                } else {
                    Timber.e(e)
                }

            }

        }

        private data class UriConfig(
                var uri: Uri? = null,
                var options: RequestOptions? = null,
                var centerCrop: Boolean? = null,
                var centerInside: Boolean? = null,
                var circleCrop: Boolean? = null,
                var fitCenter: Boolean? = null,
                var transformation: Transformation<Bitmap>? = null
        )

        private fun RequestOptions.applyIf(
                check: Boolean?,
                action: RequestOptions.() -> RequestOptions
        ): RequestOptions = if (check == true) action(this) else this

        private fun <T: Any> RequestOptions.applyIf(
                value: T?,
                action: RequestOptions.(T) -> RequestOptions
        ): RequestOptions = if (value != null) action(value) else this

    }

}

sealed class ImageSource {

    class Uri(val uri: android.net.Uri?): ImageSource()

    class Drawable(val drawable: android.graphics.drawable.Drawable?): ImageSource()

    class Resource(@DrawableRes val id: Int): ImageSource()

}