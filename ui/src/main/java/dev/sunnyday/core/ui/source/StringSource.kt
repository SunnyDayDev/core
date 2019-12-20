package dev.sunnyday.core.ui.source

import android.content.Context
import androidx.annotation.StringRes

sealed class StringSource: Source<String> {

    data class Res(@StringRes val resId: Int): StringSource() {

        override fun get(context: Context): String = context.getString(resId)

    }

    data class ResWithFormat(@StringRes val resId: Int, val args: Array<out Any>): StringSource() {

        companion object {

            fun create(@StringRes resId: Int, vararg args: Any) =
                ResWithFormat(resId, args)

        }

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