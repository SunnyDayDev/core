package dev.sunnyday.core.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnyday.dev
 */
 
class LateInitializer<T: Any> {

    internal lateinit var value: T
        private set

    @Synchronized
    fun set(value: T) {
        this.value = value
    }

}

fun <T: Any> lateinit(value: LateInitializer<T>): Lazy<T> {
    val weak = Weak(value)
    return lazy { weak.value?.value ?: throw UninitializedPropertyAccessException() }
}