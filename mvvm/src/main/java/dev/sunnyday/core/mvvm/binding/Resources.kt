package dev.sunnyday.core.mvvm.binding

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-18.
 * mail: mail@sunnydaydev.me
 */

sealed class StringSource {

    abstract fun get(context: Context): String

    data class Res(@StringRes val resId: Int): StringSource() {

        override fun get(context: Context): String = context.getString(resId)

    }

    data class ResWithFormat(@StringRes val resId: Int, val args: Array<Any>): StringSource() {

        override fun get(context: Context): String = context.getString(resId, *args)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ResWithFormat

            if (resId != other.resId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = resId
            result = 31 * result + args.contentHashCode()
            return result
        }

    }

    data class Raw(val value: String): StringSource() {

        override fun get(context: Context): String = value
    }

}

sealed class DrawableSource {

    internal abstract fun get(context: Context): Drawable

    data class Uri(val uri: android.net.Uri): DrawableSource() {

        override fun get(context: Context): Drawable = Drawable.createFromPath(uri.path)!!

    }

    data class Raw(val drawable: Drawable): DrawableSource() {

        override fun get(context: Context): Drawable = drawable

    }

    data class Res(@DrawableRes val id: Int): DrawableSource() {

        override fun get(context: Context): Drawable {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ContextCompat.getDrawable(context, id)!!

            } else {

                try {

                    ContextCompat.getDrawable(context, id)!!

                } catch (rootError: Throwable) {

                    try {
                        val theme = context.findActivity()?.theme
                        VectorDrawableCompat.create(context.resources, id, theme)!!
                    } catch (ignored: Throwable) {
                        throw rootError
                    }

                }

            }

        }

    }

}