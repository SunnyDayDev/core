package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

fun currentStackTraceElement(shift: Int = 0): StackTraceElement {

    val filteredStackTrace = Thread.currentThread().stackTrace
        .filterNot { it.methodName.endsWith("\$default") }
        .drop(2)
        .dropWhile { it.className == "dev.sunnyday.core.runtime.StackTraceKt" }

    return filteredStackTrace[shift]

}