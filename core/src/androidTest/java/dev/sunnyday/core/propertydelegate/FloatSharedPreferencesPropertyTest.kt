package dev.sunnyday.core.propertydelegate

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FloatSharedPreferencesPropertyTest: SharedPreferencePropertyTest("floatTest") {

    var SharedPreferences.named: Float by sharedPreferencesFloat("customName", 0f)
    var SharedPreferences.default: Float by sharedPreferencesFloat(default = 3f)
    
    var SharedPreferences.optional: Float? by sharedPreferencesFloat()
    

    @Test
    fun testDefaultValue() {

        val sharedPreferences = prefs

        assertEquals(3f, sharedPreferences.default)

    }

    @Test
    fun testValueChanged() {

        val sharedPreferences = prefs

        sharedPreferences.default = 5f

        assertEquals(5f, sharedPreferences.default)

    }

    @Test
    fun testName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("customName"))

        sharedPreferences.named = 0f

        assertTrue(sharedPreferences.contains("customName"))

    }

    @Test
    fun testNameByPropertyName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("default"))

        sharedPreferences.default = 0f

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

        sharedPreferences.optional = 0f

        assertTrue(sharedPreferences.contains("optional"))

        sharedPreferences.optional = null

        assertFalse(sharedPreferences.contains("optional"))

    }

}