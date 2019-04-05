package dev.sunnyday.core.util

import java.lang.ref.WeakReference
import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.SoftReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 10.08.2018.
 * mail: mail@sunnyday.dev
 */

operator fun <T> WeakReference<T>.component1(): T? = get()

operator fun <T> Reference<T>.component1(): T? = get()

operator fun <T> SoftReference<T>.component1(): T? = get()

operator fun <T> PhantomReference<T>.component1(): T? = get()