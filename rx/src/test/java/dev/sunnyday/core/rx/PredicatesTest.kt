package dev.sunnyday.core.rx

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-12.
 * mail: mail@sunnydaydev.me
 */

class PredicatesTest {

    @Test
    fun contendDeepEquals() {

        val predicate = Predicates.contendDeepEquals<List<Int>>()

        assertTrue(predicate.test(listOf(1,2,3), listOf(1,2,3)))
        assertFalse(predicate.test(listOf(1,2,3), listOf(3,2,1)))

    }

}