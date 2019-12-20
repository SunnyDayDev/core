package dev.sunnyday.core.ui.source

import android.content.Context

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-18.
 * mail: mail@sunnydaydev.me
 */

interface Source<T> {

    fun get(context: Context): T

}

