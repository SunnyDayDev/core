package dev.sunnyday.core.mvvm.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 18.08.2018.
 * mail: mail@sunnyday.dev
 */

internal class OperationNotSupportedError: Throwable("Operation not supported.")
 
internal fun notSupportedOperation(): Nothing = throw OperationNotSupportedError()