package dev.sunnyday.core.ui.source

import android.content.ContentResolver
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Base64
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import dev.sunnyday.core.ui.graphics.drawable.DrawableWrapper
import dev.sunnyday.core.ui.util.findActivity
import dev.sunnyday.core.util.Soft
import java.io.FileInputStream
import java.net.URL

sealed class DrawableSource: Source<Drawable> {

    private val softValue = Soft<Drawable>()

    final override fun get(context: Context): Drawable {
        return softValue.value ?: getDrawable(context).also(softValue::value::set)
    }

    abstract fun getDrawable(context: Context): Drawable

    data class Uri(val uri: android.net.Uri): DrawableSource() {

        private val sourceName get() = "drawable_${Base64.encodeToString(
            uri.toString().toByteArray(),
            Base64.DEFAULT
        )}"

        override fun getDrawable(context: Context): Drawable = when (uri.scheme) {

            ContentResolver.SCHEME_ANDROID_RESOURCE -> {

                if (uri.host == context.packageName) {

                    val path = uri.pathSegments
                    val firstPathItem = path[0]
                    val id = when {
                        firstPathItem.isDigitsOnly() -> firstPathItem.toInt()
                        firstPathItem == "drawable" -> context.resources
                            .getIdentifier(path.last(), "drawable", context.packageName)
                        else -> error("Unparsable uri path: ${uri.path}")
                    }

                    getDrawableById(
                        context,
                        id
                    )

                } else {

                    val inputStream = context.contentResolver.openInputStream(uri)!!
                    Drawable.createFromStream(
                        inputStream,
                        uri.toString()
                    )

                }

            }

            ContentResolver.SCHEME_CONTENT -> {
                val inputStream = context.contentResolver.openInputStream(uri)!!
                Drawable.createFromStream(
                    inputStream,
                    uri.toString()
                )
            }

            ContentResolver.SCHEME_FILE -> {
                val stream = FileInputStream(uri.path)
                Drawable.createFromStream(
                    stream,
                    sourceName
                )
            }

            "http", "https" -> {

                val wrapper =
                    DrawableWrapper()

                Thread {

                    val drawable =
                        Drawable.createFromStream(
                            URL(uri.toString()).openStream(),
                            sourceName
                        )

                    Handler(Looper.getMainLooper())
                        .post {
                        wrapper.wrappedDrawable = drawable
                    }

                } .start()

                wrapper

            }

            else -> error("Unhandable resource scheme: ${uri.scheme}")

        }


    }

    data class Raw(val drawable: Drawable): DrawableSource() {

        override fun getDrawable(context: Context): Drawable = drawable

    }

    data class Res(@DrawableRes val id: Int): DrawableSource() {

        override fun getDrawable(context: Context): Drawable =
            getDrawableById(
                context,
                id
            )

    }

    data class Bitmap(val value: android.graphics.Bitmap,
                      val config: ((BitmapDrawable) -> Unit)? = null): DrawableSource() {

        override fun getDrawable(context: Context): Drawable =
            BitmapDrawable(context.resources, value)
                .apply { config?.invoke(this) }

    }

    data class Color(@ColorInt val color: Int): DrawableSource() {

        override fun getDrawable(context: Context): Drawable =
            ColorDrawable(color)

    }

    data class ColorRes(@androidx.annotation.ColorRes val colorResId: Int): DrawableSource() {

        override fun getDrawable(context: Context): Drawable =
            ColorDrawable(
                ContextCompat.getColor(
                    context,
                    colorResId
                )
            )

    }

    companion object {

        @Suppress("NOTHING_TO_INLINE")
        inline operator fun invoke(drawable: Drawable) = Raw(drawable)

        @Suppress("NOTHING_TO_INLINE")
        inline operator fun invoke(
            value: android.graphics.Bitmap,
            noinline config: ((BitmapDrawable) -> Unit)? = null
        ) = Bitmap(value, config)

        @Suppress("NOTHING_TO_INLINE")
        inline operator fun invoke(uri: android.net.Uri) = Uri(uri)

        @Suppress("NOTHING_TO_INLINE")
        inline operator fun invoke(@DrawableRes res: Int) = Res(res)

        fun getDrawableById(context: Context, @DrawableRes resId: Int): Drawable {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ContextCompat.getDrawable(context, resId)!!

            } else {

                try {

                    ContextCompat.getDrawable(
                        context,
                        resId
                    )!!

                } catch (rootError: Throwable) {

                    try {
                        val theme = context.findActivity()?.theme
                        VectorDrawableCompat.create(
                            context.resources,
                            resId,
                            theme
                        )!!
                    } catch (ignored: Throwable) {
                        throw rootError
                    }

                }

            }

        }

    }

}