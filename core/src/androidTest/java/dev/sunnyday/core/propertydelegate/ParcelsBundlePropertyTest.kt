package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ParcelsBundlePropertyTest {

    var Bundle.named: Int by bundleParcels("test", 0)
    var Bundle.default: Int by bundleParcels(default = 3)
    var Bundle.optional: Int? by bundleParcels()

    @Test
    fun testDefaultValue() {

        val bundle = Bundle()

        assertEquals(3, bundle.default)

    }

    @Test
    fun testValueChanged() {

        val bundle = Bundle()

        bundle.default = 5

        assertEquals(5, bundle.default)

    }

    @Test
    fun testName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("test"))

        bundle.named = 0

        assertTrue(bundle.containsKey("test"))

    }

    @Test
    fun testNameByPropertyName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("default"))

        bundle.default = 0

        assertTrue(bundle.containsKey("default"))

    }

    @Test
    fun optionalIsNullWhenUnexists() {

        val bundle = Bundle()
        assertNull(bundle.optional)

    }

    @Test
    fun optionalRemoveKeyIfSetNull() {

        val bundle = Bundle()

        bundle.optional = 1

        assertTrue(bundle.containsKey("optional"))

        bundle.optional = null

        assertFalse(bundle.containsKey("optional"))

    }

}