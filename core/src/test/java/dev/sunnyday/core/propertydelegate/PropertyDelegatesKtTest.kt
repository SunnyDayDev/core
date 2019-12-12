package dev.sunnyday.core.propertydelegate

import dev.sunnyday.core.testUtils.MemoryUtil
import dev.sunnyday.core.testUtils.ReadWritePropertyDelegateTester
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */

class PropertyDelegatesKtTest {

    @Test
    fun testWeak() {

        val tester = ReadWritePropertyDelegateTester<Any?> { weak() }

        assert(tester.value == null)

        tester.value = Any()

        assert(tester.value != null)

        System.gc()

        assert(tester.value == null)

    }

    @Test
    fun testSoft() {

        val tester = ReadWritePropertyDelegateTester<Any?> { soft() }

        assert(tester.value == null)

        tester.value = Any()

        assert(tester.value != null)

        System.gc()

        assert(tester.value != null)

        MemoryUtil.eatAllMemory()

        assert(tester.value == null)

    }

}