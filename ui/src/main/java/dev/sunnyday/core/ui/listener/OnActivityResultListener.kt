package dev.sunnyday.core.ui.listener

import android.content.Intent
import dev.sunnyday.core.collections.sortedSetOf

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
interface OnActivityResultListener {

    val onActivityResultHandlingPriority: Int get() = 0

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean

    interface Registry: OnActivityResultListener {

        interface Owner {

            val onActivityResultRegistry: Registry

        }

        fun add(listener: OnActivityResultListener)

        fun remove(listener: OnActivityResultListener)

    }

}

class DefaultOnActivityResultRegistry(override val onActivityResultHandlingPriority: Int = 0): OnActivityResultListener.Registry {

    private var listeners = sortedSetOf<OnActivityResultListener, Int>(ascending = false) {
        it.onActivityResultHandlingPriority
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean =
        listeners.find { it.onActivityResult(requestCode, resultCode, data) } != null

    override fun add(listener: OnActivityResultListener) {
        listeners.add(listener)
    }

    override fun remove(listener: OnActivityResultListener) {
        listeners.remove(listener)
    }

}