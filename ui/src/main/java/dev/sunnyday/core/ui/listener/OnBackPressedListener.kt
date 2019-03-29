package dev.sunnyday.core.ui.listener

import dev.sunnyday.core.collections.sortedSetOf

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
interface OnBackPressedListener {

    val onBackPressedHandlingPriority: Int get() = 0

    fun onBackPressed(): Boolean

    interface Registry: OnBackPressedListener {

        interface Owner {

            val onBackPressedRegistry: OnBackPressedListener.Registry

        }

        fun add(listener: OnBackPressedListener)

        fun remove(listener: OnBackPressedListener)

    }

}

class DefaultOnBackPressedRegistry(override val onBackPressedHandlingPriority: Int = 0): OnBackPressedListener.Registry {

    private var listeners = sortedSetOf<OnBackPressedListener, Int>(ascending = false) {
        it.onBackPressedHandlingPriority
    }

    override fun onBackPressed(): Boolean = listeners.find { it.onBackPressed() } != null

    override fun add(listener: OnBackPressedListener) {
        listeners.add(listener)
    }

    override fun remove(listener: OnBackPressedListener) {
        listeners.remove(listener)
    }

}