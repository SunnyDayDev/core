package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DoubleBundlePropertyTest {

    var Bundle.named: Double by bundleDouble("test", 0.0)
    var Bundle.default: Double by bundleDouble(default = 3.0)
    var Bundle.optional: Double? by bundleDouble()

    @Test
    fun testDefaultValue() {

        val bundle = Bundle()

        assertEquals(3.0, bundle.default, 0.1)

    }

    @Test
    fun testValueChanged() {

        val bundle = Bundle()

        bundle.default = 5.0

        assertEquals(5.0, bundle.default, 0.1)

    }

    @Test
    fun testName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("test"))

        bundle.named = 0.0

        assertTrue(bundle.containsKey("test"))

    }

    @Test
    fun testNameByPropertyName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("default"))

        bundle.default = 0.0

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

        bundle.optional = 1.0

        assertTrue(bundle.containsKey("optional"))

        bundle.optional = null

        assertFalse(bundle.containsKey("optional"))

    }

}