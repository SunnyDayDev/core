package dev.sunnyday.core.util

import android.content.Context

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-25.
 * mail: mail@sunnydaydev.me
 */
 
object AppGlobals {

    lateinit var applicationContext: Context
        private set

    fun init(context: Context) {
        applicationContext = context.applicationContext
    }

}