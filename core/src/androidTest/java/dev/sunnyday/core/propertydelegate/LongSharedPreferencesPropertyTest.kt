package dev.sunnyday.core.propertydelegate

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LongSharedPreferencesPropertyTest: SharedPreferencePropertyTest("longTest") {

    var SharedPreferences.named: Long by sharedPreferencesLong("customName", 0)
    var SharedPreferences.default: Long by sharedPreferencesLong(default = 3)
    
    var SharedPreferences.optional: Long? by sharedPreferencesLong()

    @Test
    fun testDefaultValue() {

        val sharedPreferences = prefs

        assertEquals(3, sharedPreferences.default)

    }

    @Test
    fun testValueChanged() {

        val sharedPreferences = prefs

        sharedPreferences.default = 5

        assertEquals(5, sharedPreferences.default)

    }

    @Test
    fun testName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("customName"))

        sharedPreferences.named = 0

        assertTrue(sharedPreferences.contains("customName"))

    }

    @Test
    fun testNameByPropertyName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("default"))

        sharedPreferences.default = 0

        assertTrue(sharedPreferences.contains("default"))

    }

    @Test
    fun optionalIsNullWhenUnexists() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("optional"))
        assertNull(sharedPreferences.optional)

    }

    @Test
    fun optionalRemoveKeyIfSetNull() {

        val sharedPreferences = prefs

        sharedPreferences.optional = 0

        assertTrue(sharedPreferences.contains("optional"))

        sharedPreferences.optional = null

        assertFalse(sharedPreferences.contains("optional"))

    }

}