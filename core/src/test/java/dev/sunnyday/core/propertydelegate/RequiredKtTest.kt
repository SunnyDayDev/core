package dev.sunnyday.core.propertydelegate

import org.junit.Test

import org.junit.Assert.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-07.
 * mail: mail@sunnydaydev.me
 */

class RequiredKtTest {

    @Test
    fun testSetValue() {

        val testCase = IntTestCase(null)

        testCase.requiredValue = 7

        assertEquals(7, testCase.value)

    }

    @Test
    fun testGetNonNullValue() {

        val testCase = IntTestCase(7)

        assertEquals(7, testCase.requiredValue)

    }

    @Test(expected = RequiredPropertyIsNull::class)
    fun testFailOnGetIfNullValue() {

        val testCase = IntTestCase(null)

        testCase.requiredValue

    }

    private class IntTestCase(initialValue: Int?) {

        private val propertyDelegate = VarProperty<IntTestCase, Int>(initialValue)

        var value by propertyDelegate

        var requiredValue by required(propertyDelegate)

    }

    private class VarProperty<R, T>(var value: T?): ReadWriteProperty<R, T?> {

        override fun getValue(thisRef: R, property: KProperty<*>): T? = value

        override fun setValue(thisRef: R, property: KProperty<*>, value: T?) {
            this.value = value
        }

    }

}