package dev.sunnyday.core.mvvm.binding

import android.view.View
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test

import org.junit.Assert.*

class ViewExtrasTest : Bindings() {

    @Test
    fun testThatExtrasAlwaysTheSame() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val initialExtras = testView.extras

        assertEquals(initialExtras, testView.extras)

        Thread {
            Thread.sleep(100)
            assertEquals(initialExtras, testView.extras)
        }.run()
    }

    @Test
    fun testExtrasReturnsSettedItem() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = Any()
        val testKey = "testKey"

        testExtras[testKey] = testValue
        assertEquals(testExtras[testKey], testValue)
    }

    @Test
    fun testExtrasReturnsNullIfCantCast() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = Any()
        val testKey = "testKey"

        testExtras[testKey] = testValue

        val resultValue: Int? = testExtras[testKey]
        assertNull(resultValue)
    }

    @Test
    fun testExtrasReturnsNullIfKeyNotExists() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testKey = "testKey"

        assertNull(testExtras[testKey])
    }

    @Test
    fun testExtrasRemovesValueIfNullSet() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = Any()
        val testKey = "testKey"

        assertEquals(testExtras.size, 0)

        testExtras[testKey] = testValue
        assertEquals(testExtras.size, 1)

        testExtras[testKey] = null
        assertEquals(testExtras.size, 0)
    }

    @Test
    fun testExtrasGetOrSetSetIfEmpty() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = Any()
        val testKey = "testKey"

        assertEquals(testExtras.getOrSet(testKey) { testValue }, testValue)
        assertEquals(testExtras[testKey], testValue)
    }

    @Test
    fun testExtrasGetOrSetSetIfCantCast() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = 5
        val testKey = "testKey"

        testExtras[testKey] = Any()

        assertEquals(testExtras.getOrSet(testKey) { testValue }, testValue)
        assertEquals(testExtras[testKey], testValue)
    }

    @Test
    fun testExtrasGetOrSetReturnsStoredIfHas() {
        val testView = View(InstrumentationRegistry.getInstrumentation().context)
        val testExtras = testView.extras
        val testValue = 5
        val otherValue = 7
        val testKey = "testKey"

        testExtras[testKey] = testValue

        assertEquals(testExtras.getOrSet(testKey) { otherValue }, testValue)
        assertEquals(testExtras[testKey], testValue)
    }

}