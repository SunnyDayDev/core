package dev.sunnyday.core.ui.util

import android.content.Context
import android.widget.Toast

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 30.07.2018.
 * mail: mail@sunnyday.dev
 */

interface Toaster {

    fun makeShort(text: String)
    fun makeLong(text: String)

    companion object {

        fun create(context: Context): Toaster = DefaultToaster(context)

    }

}

private class DefaultToaster constructor(private val context: Context) : Toaster {

    override fun makeShort(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun makeLong(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

}