package dev.sunnyday.core.util

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-05.
 * mail: mail@sunnydaydev.me
 */

class ResultCallbackKtTest {

    @Test
    fun successUnit() {

        var resultIsSuccess = false

        val callback: ResultCallback<Unit> =  { result ->
            resultIsSuccess = result.isSuccess
        }

        callback.success()

        assertTrue(resultIsSuccess)

    }

    @Test
    fun successValue() {

        var resultIsSuccess = false
        var expectedValue = false

        val targetValue = 1001

        val callback: ResultCallback<Int> =  { result ->
            resultIsSuccess = result.isSuccess
            expectedValue = result.getOrThrow() == targetValue
        }

        callback.success(targetValue)

        assertTrue(resultIsSuccess)
        assertTrue(expectedValue)

    }

    @Test
    fun failure() {

        var resultIsFailure = false
        var expectedError = false

        val targetError = Error("Test error.")

        val callback: ResultCallback<Int> =  { result ->
            resultIsFailure = result.isFailure
            expectedError = result.exceptionOrNull() == targetError
        }

        callback.failure(targetError)

        assertTrue(resultIsFailure)
        assertTrue(expectedError)

    }

}