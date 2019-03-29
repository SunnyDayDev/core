package dev.sunnyday.core.collections

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

class SortedKtTest {

    @Test
    fun `sorted set has right order`() {

        val ascendingSet = sortedSetOf(3, 2, 4, 1, 5) { it }
        val descendingSet = sortedSetOf(3, 2, 4, 1, 5, ascending = false) { it }

        val etalon = setOf(1, 2, 3, 4, 5)

        assert(etalon contentEquals ascendingSet)
        assert(etalon.reversed() contentEquals descendingSet)

    }

}