package dev.sunnyday.core.propertydelegate

import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-07.
 * mail: mail@sunnydaydev.me
 */

class RequiredKtTest {

    private val random = Random(System.currentTimeMillis())

    @Test
    fun testSetValue() {

        val value = random.nextInt()

        val testCase = IntTestCase(null)

        testCase.requiredValue = value

        assertEquals(value, testCase.value)

    }

    @Test
    fun testGetNonNullValue() {

        val value = random.nextInt()

        val testCase = IntTestCase(value)

        assertEquals(value, testCase.requiredValue)

    }

    @Test(expected = RequiredPropertyIsNull::class)
    fun testFailOnGetIfNullValue() {

        val testCase = IntTestCase(null)

        testCase.requiredValue

    }

    private class IntTestCase(var value: Int?) {

        var requiredValue by required(delegateTo(this::value))

    }

}