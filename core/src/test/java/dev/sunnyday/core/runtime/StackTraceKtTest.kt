package dev.sunnyday.core.runtime

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

class StackTraceKtTest {

    @Test
    fun currentStackTraceElementTest() {

        val elements = arrayOf(
            currentStackTraceElement(),
            currentStackTraceElement(shift = 0),
            Any().stackTraceElement(),
            Any().stackTraceElement2(),
            Any().stackTraceElement2(0),
            Any().stackTraceElement3(),
            Any().stackTraceElement3(0),
            Any().stackTraceElement4(),
            Any().stackTraceElement4(0)
        )

        elements.forEach { println("$it") }
        elements.forEach { assert(it.methodName == elements[0].methodName) }

    }


    fun Any.stackTraceElement(): StackTraceElement {
        return currentStackTraceElement(shift = 1)
    }

    private fun Any.stackTraceElement2(value: Int = 0, stackTraceElement: StackTraceElement = currentStackTraceElement()): StackTraceElement =
        stackTraceElement

    private fun Any.stackTraceElement3(value: Int = 0, stackTraceElement: StackTraceElement = currentStackTraceElement(shift = 1)): StackTraceElement =
        stackTraceElement

    fun Any.stackTraceElement4(value: Int = 0): StackTraceElement {
        return currentStackTraceElement(shift = 1)
    }

}