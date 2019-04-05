package dev.sunnyday.core.util

import java.lang.ref.WeakReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 20.08.2018.
 * mail: mail@sunnyday.dev
 */

class Weak<T>(value: T? = null) {

    private var weakRef: WeakReference<T>? = null

    var value: T?
        get() = synchronized(this) { weakRef?.get() }
        set(value) = synchronized(this) {
            weakRef = when {
                value == null -> null
                value === this.value -> weakRef
                else -> WeakReference(value)
            }
        }

    init {
        this.value = value
    }

    operator fun component1(): T? = value

    inline fun getOrSet(provider: () -> T): T = synchronized(this) {
        value ?: provider().also { value = it }
    }

    inline fun doIfPresent(action: (T) -> Unit) {
        val currentValue = value
        if (currentValue != null) {
            action(currentValue)
        }
    }

}