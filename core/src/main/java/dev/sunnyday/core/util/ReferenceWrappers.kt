package dev.sunnyday.core.util

import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 20.08.2018.
 * mail: mail@sunnyday.dev
 */

class Weak<T: Any>(value: T? = null): ReferenceWrapper<T>(value) {

    override fun wrap(value: T) = WeakReference(value)

}

class Soft<T: Any>(value: T? = null): ReferenceWrapper<T>(value) {

    override fun wrap(value: T) = SoftReference(value)

}

abstract class ReferenceWrapper<T: Any>(value: T? = null) {

    private var ref: Reference<T>? = null

    var value: T?
        get() = synchronized(this) { ref?.get() }
        set(value) = synchronized(this) {
            ref = when {
                value == null -> null
                value === this.value -> ref
                else -> wrap(value)
            }
        }

    init {
        this.value = value
    }

    protected abstract fun wrap(value: T): Reference<T>

    operator fun component1(): T? = value

    inline fun getOrSet(provider: () -> T): T = synchronized(this) {
        value ?: provider().also { value = it }
    }

    inline fun ifPresent(action: (T) -> Unit) {
        val currentValue = value
        if (currentValue != null) {
            action(currentValue)
        }
    }

}