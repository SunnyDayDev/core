package dev.sunnyday.core.collections

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-12.
 * mail: mail@sunnydaydev.me
 */

class EqualsKtTest {

    private val arrayOne: Array<Int> get() = arrayOf(1,5,7)

    private val arrayTwo: Array<Int> get() = arrayOf(2,3,6)

    private val listOne: List<Int> get() = listOf(1,5,7)

    private val listTwo: List<Int> get() = listOf(2,3,6)

    @Test
    fun collectionContentDeepEquals() {

        assertTrue(listOne contentDeepEquals listOne)
        assertTrue(listOne.map(::TestData) contentDeepEquals listOne.map(::TestData))
        assertTrue(listOne.map(::TestArrayData) contentDeepEquals listOne.map(::TestArrayData))

        assertFalse(listOne contentDeepEquals listTwo)
        assertFalse(listOne.map(::TestData) contentDeepEquals listTwo.map(::TestData))
        assertFalse(listOne.map(::TestArrayData) contentDeepEquals listTwo.map(::TestArrayData))

    }

    @Test
    fun collectionContentNotDeepEquals() {

        assertFalse(listOne contentNotDeepEquals  listOne)
        assertFalse(listOne.map(::TestData) contentNotDeepEquals listOne.map(::TestData))
        assertFalse(listOne.map(::TestArrayData) contentNotDeepEquals listOne.map(::TestArrayData))

        assertTrue(listOne contentNotDeepEquals listTwo)
        assertTrue(listOne.map(::TestData) contentNotDeepEquals listTwo.map(::TestData))
        assertTrue(listOne.map(::TestArrayData) contentNotDeepEquals listTwo.map(::TestArrayData))

    }

    @Test
    fun collectionContentEquals() {

        assertTrue(listOne contentEquals listOne)
        assertTrue(listOne.map(::TestData) contentEquals listOne.map(::TestData))
        assertTrue(listOne.map(::TestArrayData) contentEquals listOne.map(::TestArrayData))

        assertFalse(listOne contentEquals listTwo)
        assertFalse(listOne.map(::TestData) contentEquals listTwo.map(::TestData))
        assertFalse(listOne.map(::TestArrayData) contentEquals listTwo.map(::TestArrayData))

    }

    @Test
    fun collectionContentNotEquals() {

        assertFalse(listOne contentNotEquals listOne)
        assertFalse(listOne.map(::TestData) contentNotEquals listOne.map(::TestData))
        assertFalse(listOne.map(::TestArrayData) contentNotEquals listOne.map(::TestArrayData))

        assertTrue(listOne contentNotEquals listTwo)
        assertTrue(listOne.map(::TestData) contentNotEquals listTwo.map(::TestData))
        assertTrue(listOne.map(::TestArrayData) contentNotEquals listTwo.map(::TestArrayData))

    }

    @Test
    fun arrayContentNotDeepEquals() {
        assertTrue(arrayOne contentNotDeepEquals arrayTwo)
    }

    @Test
    fun arrayContentNotEquals() {
        assertTrue(arrayOne contentNotEquals arrayTwo)
    }

    data class TestData(val value: Int)


    data class TestArrayData(val value: List<Int>) {

        constructor(value: Int): this((0..5).map { value + it })

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as TestArrayData

            if (value contentNotDeepEquals other.value) return false

            return true
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

    }

}