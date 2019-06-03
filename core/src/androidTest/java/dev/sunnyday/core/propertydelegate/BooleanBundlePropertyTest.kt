package dev.sunnyday.core.propertydelegate

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BooleanBundlePropertyTest {

    var Bundle.named: Boolean by bundleBoolean("test", false)
    var Bundle.defaultTrue: Boolean by bundleBoolean(default = true)
    var Bundle.defaultFalse: Boolean by bundleBoolean(default = false)
    
    var Bundle.optional: Boolean? by bundleBoolean()

    @Test
    fun testDefaultValue() {

        val bundle = Bundle()

        assertFalse(bundle.defaultFalse)
        assertTrue(bundle.defaultTrue)

    }

    @Test
    fun testValueChanged() {

        val bundle = Bundle()

        bundle.defaultTrue = false
        bundle.defaultFalse = true

        assertFalse(bundle.defaultTrue)
        assertTrue(bundle.defaultFalse)

    }

    @Test
    fun testName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("test"))

        bundle.named = true

        assertTrue(bundle.containsKey("test"))

    }

    @Test
    fun testNameByPropertyName() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("defaultTrue"))

        bundle.defaultTrue = true

        assertTrue(bundle.containsKey("defaultTrue"))

    }

    @Test
    fun optionalIsNullWhenUnexists() {

        val bundle = Bundle()

        assertFalse(bundle.containsKey("optional"))
        assertNull(bundle.optional)

    }

    @Test
    fun optionalRemoveKeyIfSetNull() {

        val bundle = Bundle()

        bundle.optional = true

        assertTrue(bundle.containsKey("optional"))

        bundle.optional = null

        assertFalse(bundle.containsKey("optional"))

    }

}