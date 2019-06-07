package dev.sunnyday.core.runtime

import org.junit.Test
import org.junit.Assert.assertEquals
import java.lang.Error

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-07.
 * mail: mail@sunnydaydev.me
 */

class CombineKtTest {

    @Test
    fun testCombine2properlyMap() {

        val initialSource = "5,0"

        val firstItem = { source: String ->
            source.split(",").first()
        }

        val toInt = { source: String ->
            source.toInt()
        }

        val combined = combine(firstItem, toInt)

        assertEquals(5, combined(initialSource))

    }

    @Test
    fun testCombine3properlyMap() {


        val initialSource = "5,0"

        val firstItem = { source: String ->
            source.split(",").first()
        }

        val toInt = { source: String ->
            source.toInt()
        }

        val to2Prime = { source: Int ->
            source * source
        }

        val combined = combine(firstItem, toInt, to2Prime)

        assertEquals(25, combined(initialSource))

    }

}