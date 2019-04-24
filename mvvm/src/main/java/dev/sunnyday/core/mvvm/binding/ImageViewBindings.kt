package dev.sunnyday.core.mvvm.binding

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingConversion
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.request.RequestOptions
import dev.sunnyday.core.mvvm.R
import dev.sunnyday.core.runtime.alsoDo
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.net.URL


/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnyday.dev
 */

object ImageViewBindings: Bindings() {

    // region Bindings

    @JvmStatic
    @BindingAdapter("imageSource")
    fun bindImageSource(view: ImageView, source: BindableSource<Drawable>?) =
        view.core.setSource(source)

    @JvmStatic
    @BindingAdapter("imageUriForceUseLoader")
    fun bindImageUriForceUseLoader(view: ImageView, forceUseLoader: Boolean) =
        view.core.setForceUseLoader(forceUseLoader)

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
    fun bindSrcCompat(view: ImageView, @DrawableRes id: Int) = bindSrc(view, id)

    @JvmStatic
    @BindingAdapter("src")
    fun bindSrc(view: ImageView, @DrawableRes id: Int) = bindImageSource(view, convertResIdToSource(id))

    // endregion

    // region: Conversion

    @JvmStatic
    @BindingConversion
    fun convertUriToSource(uri: Uri?): BindableSource<Drawable>? = uri?.let(DrawableSource::Uri)

    @JvmStatic
    @BindingConversion
    fun convertURLToSource(url: URL?): BindableSource<Drawable>? {
        url ?: return null
        val uri = Uri.parse(url.toString())
        return convertUriToSource(uri)
    }

    @JvmStatic
    @BindingConversion
    fun convertStringToSource(string: String?): BindableSource<Drawable>? {
        string ?: return null
        val uri = Uri.parse(string)
        return convertUriToSource(uri)
    }

    @JvmStatic
    @BindingConversion
    fun convertResIdToSource(@DrawableRes resId: Int?): BindableSource<Drawable>? = resId?.let(DrawableSource::Res)

    @JvmStatic
    @BindingConversion
    fun convertDrawableToSource(drawable: Drawable?): BindableSource<Drawable>? = drawable?.let(DrawableSource::Raw)

    @JvmStatic
    @BindingConversion
    fun convertBitmapToSource(bitmap: Bitmap?): BindableSource<Drawable>? = bitmap?.let { DrawableSource.Bitmap(it) }

    // endregion

    // region Helpers

    private val ImageView.core get() =
        getOrSetListener(R.id.binding_imageview_source_core) { ImageSourceCore(this) }

    private class ImageSourceCore(view: ImageView): BindableCore<ImageView, BindableCore.Change.Simple>(view) {

        private val uriConfig by lazy { UriConfig() alsoDo {
            uriConfigInitialized = true
        } }
        private var source: BindableSource<Drawable>? = null

        private var isGlideUsed = false
        private var uriConfigInitialized = false

        fun setSource(source: BindableSource<Drawable>?) {
            if (source == this.source) return
            this.source = source
            notifyChanges()
        }

        fun setUriCenterCrop(centerCrop: Boolean?) {
            if (uriConfig.centerCrop == centerCrop) return
            uriConfig.centerCrop = centerCrop
            notifyChanges()
        }

        fun setUriCenterInside(centerInside: Boolean?) {
            if (uriConfig.centerInside == centerInside) return
            uriConfig.centerInside = centerInside
            notifyChanges()
        }

        fun setUriCircleCrop(circleCrop: Boolean?) {
            if (uriConfig.circleCrop == circleCrop) return
            uriConfig.circleCrop = circleCrop
            notifyChanges()
        }

        fun setUriFitCenter(fitCenter: Boolean?) {
            if (uriConfig.fitCenter == fitCenter) return
            uriConfig.fitCenter = fitCenter
            notifyChanges()
        }

        fun setUriTransformation(transformation: Transformation<Bitmap>?) {
            if (uriConfig.transformation == transformation) return
            uriConfig.transformation = transformation
            notifyChanges()
        }

        fun setUriOptions(options: RequestOptions?) {
            if (uriConfig.options == options) return
            uriConfig.options = options
            notifyChanges()
        }

        fun setForceUseLoader(forceUseLoader: Boolean) {
            if (uriConfig.forceUseLoader == forceUseLoader) return
            uriConfig.forceUseLoader = forceUseLoader
            notifyChanges()
        }

        override fun applyChanges(changes: List<Change.Simple>) {

            clearGlideIfWasUsed()

            when (val source = this.source) {
                is DrawableSource.Uri -> applyUriSource(source)
                else -> applyCommonSource()
            }

        }

        private fun applyUriSource(source: DrawableSource.Uri) {
            if (uriConfigInitialized && uriConfig.forceUseLoader) {
                applyUriByLoader(source.uri)
            } else {
                when (source.uri.scheme) {
                    "http", "https" -> applyUriByLoader(source.uri)
                    else -> applyCommonSource()
                }
            }
        }

        private fun applyCommonSource() {
            view.setImageDrawable(source?.get(view.context))
        }

        private fun clearGlideIfWasUsed() {
            if (isGlideUsed) {
                Glide.with(view).clear(view)
                isGlideUsed = false
            }
        }

        private fun applyUriByLoader(uri: Uri) {
            try {

                isGlideUsed = true

                val glide = Glide.with(view)

                val uriConfig = this.uriConfig.copy()

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
                var transformation: Transformation<Bitmap>? = null,
                var forceUseLoader: Boolean = false
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

    // endregion

}