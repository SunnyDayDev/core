package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FloatBundlePropertyTest {

    var Bundle.named: Float by bundleFloat("test", 0f)
    var Bundle.default: Float by bundleFloat(default = 3f)
    var Bundle.optional: Float? by bundleFloat()

    @Test
    fun testDefaultValue() {

        val bundle = Bundle()

        assertEquals(3f, bundle.default)

    }

    @Test
    fun testValueChanged() {

        val bundle = Bundle()

        bundle.default = 5f

        assertEquals(5f, bundle.default)

    }

    @Test
    fun testName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("test"))

        bundle.named = 0f

        assertTrue(bundle.containsKey("test"))

    }

    @Test
    fun testNameByPropertyName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("default"))

        bundle.default = 0f

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

        bundle.optional = 1f

        assertTrue(bundle.containsKey("optional"))

        bundle.optional = null

        assertFalse(bundle.containsKey("optional"))

    }

}