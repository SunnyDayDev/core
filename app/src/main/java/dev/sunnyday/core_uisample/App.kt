package dev.sunnyday.core_uisample

import android.app.Application
import dev.sunnyday.core.rx.RxDebug
import dev.sunnyday.core.util.AppGlobals
import timber.log.Timber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-25.
 * mail: mail@sunnydaydev.me
 */
 
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        AppGlobals.init(this)
        Timber.plant(Timber.DebugTree())
        RxDebug.enabled
    }

}