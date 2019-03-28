package dev.sunnyday.core.util

import java.lang.ref.WeakReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 20.08.2018.
 * mail: mail@sunnyday.dev
 */

class Weak<T>(value: T? = null) {

    private var weakRef: WeakReference<T>? = null

    @get:Synchronized
    @set:Synchronized
    var value: T?
        get() = weakRef?.get()
        set(value) {
            weakRef = when {
                value == null -> null
                value === this.value -> weakRef
                else -> WeakReference(value)
            }
        }

    init {
        this.value = value
    }

    @Synchronized
    fun getOrSet(provider: () -> T): T = value ?: provider().also { value = it }

    inline fun doIfPresent(action: (T) -> Unit) {
        val currentValue = this.value
        if (currentValue != null) {
            action(currentValue)
        }
    }

}