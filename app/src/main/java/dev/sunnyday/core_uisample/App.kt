package dev.sunnyday.core_uisample

import android.app.Application
import dev.sunnyday.core.util.AppGlobals

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-25.
 * mail: mail@sunnydaydev.me
 */
 
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AppGlobals.init(this)
    }

}