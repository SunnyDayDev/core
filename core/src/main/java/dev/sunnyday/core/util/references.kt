package dev.sunnyday.core.util

import java.lang.ref.WeakReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 10.08.2018.
 * mail: mail@sunnyday.dev
 */
 
operator fun <T> WeakReference<T>.component1(): T? = get()