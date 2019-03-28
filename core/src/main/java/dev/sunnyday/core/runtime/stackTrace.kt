package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

fun currentStackTraceElement(shift: Int = 0): StackTraceElement {

    return Thread.currentThread().stackTrace
        .drop(2 + shift)
        .first { !it.methodName.endsWith("\$default") }

}