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
            Extension().currentStackTraceElement(),
            Extension().currentStackTraceElement2(),
            Extension().currentStackTraceElement2(0),
            Extension().currentStackTraceElement4(),
            Extension().currentStackTraceElement4(0)
        )

        elements.forEachIndexed { i, it ->
            assert(it.methodName == "currentStackTraceElementTest")
            assert(it.lineNumber == elements[0].lineNumber + i)
        }

    }
    
    private class Extension

    private fun Extension.currentStackTraceElement(): StackTraceElement {
        return currentStackTraceElement(shift = 1)
    }

    private fun Extension.currentStackTraceElement2(
        value: Int = 0,
        stackTraceElement: StackTraceElement = currentStackTraceElement()
    ): StackTraceElement = stackTraceElement

    private fun Extension.currentStackTraceElement4(value: Int = 0): StackTraceElement {
        return currentStackTraceElement(shift = 1)
    }

}