package dev.sunnyday.core.dagger.extra

import javax.inject.Provider
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

fun <T> inject(provider: () -> Provider<T>) = lazy { provider().get() }

fun <T> provider(provider: () -> Provider<T>): ReadOnlyProperty<Any, T> = object : ReadOnlyProperty<Any, T> {

    private val lazyProvider by lazy { provider() }

    override fun getValue(thisRef: Any, property: KProperty<*>): T = lazyProvider.get()

}