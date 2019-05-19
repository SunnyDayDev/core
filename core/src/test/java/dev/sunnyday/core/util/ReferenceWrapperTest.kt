package dev.sunnyday.core.util

import org.junit.Test
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-05-19.
 * mail: mail@sunnydaydev.me
 */

class ReferenceWrapperTest {

    @Test
    fun weakIsWeakenAfterRelease() {

        var value: Any? = Any()
        val weak = Weak(Any())

        val weakRef = WeakReference(value)
        assert(weak.value != null)
        assert(weakRef.get() != null)

        @Suppress("UNUSED_VALUE")
        value = null

        System.gc()

        assert(weakRef.get() == null)
        assert(weak.value == null)

    }

    @Test
    fun softIsNotWeakenAfterRelease() {

        var value: Any? = Any()
        val soft = Soft(value)
        val softRef = SoftReference(value)

        assert(soft.value != null)
        assert(softRef.get() != null)

        @Suppress("UNUSED_VALUE")
        value = null

        System.gc()

        assert(softRef.get() != null)
        assert(soft.value != null)

    }

    @Test
    fun softIsWeakenWhenNeedMoreMemory() {

        var value: Any? = Any()
        val soft = Soft(value)
        val softRef = SoftReference(value)

        assert(soft.value != null)
        assert(softRef.get() != null)

        @Suppress("UNUSED_VALUE")
        value = null

        try {
            (0..Int.MAX_VALUE).map { ByteArray(Int.MAX_VALUE - 2) }
        }
        catch (ignored: OutOfMemoryError) { }

        assert(softRef.get() == null)
        assert(soft.value == null)

    }

}