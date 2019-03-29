package dev.sunnyday.core.ui.listener

import dev.sunnyday.core.collections.sortedSetOf

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
interface OnRequestPermissionResultListener {

    val onRequestPermissionResultHandlingPriority: Int get() = 0

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean

    interface Registry: OnRequestPermissionResultListener {

        interface Owner {

            val onRequestPermissionResultRegistry: OnRequestPermissionResultListener.Registry

        }

        fun add(listener: OnRequestPermissionResultListener)

        fun remove(listener: OnRequestPermissionResultListener)

    }

}

class DefaultOnRequestPermissionResultRegistry(
    override val onRequestPermissionResultHandlingPriority: Int = 0
): OnRequestPermissionResultListener.Registry {

    private var listeners = sortedSetOf<OnRequestPermissionResultListener, Int>(ascending = false) {
        it.onRequestPermissionResultHandlingPriority
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray): Boolean =
        listeners.find { it.onRequestPermissionsResult(requestCode, permissions, grantResults) } != null

    override fun add(listener: OnRequestPermissionResultListener) {
        listeners.add(listener)
    }

    override fun remove(listener: OnRequestPermissionResultListener) {
        listeners.remove(listener)
    }

}