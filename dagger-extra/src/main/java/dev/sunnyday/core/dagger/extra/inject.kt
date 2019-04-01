package dev.sunnyday.core.dagger.extra

import javax.inject.Provider

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

fun <T> inject(provider: () -> Provider<T>) = lazy { provider().get() }