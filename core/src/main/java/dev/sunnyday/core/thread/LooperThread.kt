package dev.sunnyday.core.thread

import android.os.Handler
import android.os.Looper

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */

class LooperThread: Thread() {

    lateinit var handler: Handler
        private set

    override fun run() {
        Looper.prepare()
        handler = Handler()
        Looper.loop()
    }

}