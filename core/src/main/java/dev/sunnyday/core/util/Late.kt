package dev.sunnyday.core.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnyday.dev
 */
 
class Late<T: Any> {

    @set:Synchronized
    internal lateinit var value: T

}

fun <T: Any> lateinit(value: Late<T>): Lazy<T> {
    val weak = Weak(value)
    return lazy { weak.value?.value ?: throw UninitializedPropertyAccessException() }
}