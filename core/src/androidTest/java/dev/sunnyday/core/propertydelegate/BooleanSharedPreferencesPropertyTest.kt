package dev.sunnyday.core.propertydelegate

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BooleanSharedPreferencesPropertyTest: SharedPreferencePropertyTest("booleanTest") {

    var SharedPreferences.named: Boolean by sharedPreferencesBoolean("customName", false)
    var SharedPreferences.defaultTrue: Boolean by sharedPreferencesBoolean(default = true)
    var SharedPreferences.defaultFalse: Boolean by sharedPreferencesBoolean(default = false)
    
    var SharedPreferences.optional: Boolean? by sharedPreferencesBoolean()

    @Test
    fun testDefaultValue() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.defaultFalse)
        assertTrue(sharedPreferences.defaultTrue)

    }

    @Test
    fun testValueChanged() {

        val sharedPreferences = prefs

        sharedPreferences.defaultTrue = false
        sharedPreferences.defaultFalse = true

        assertFalse(sharedPreferences.defaultTrue)
        assertTrue(sharedPreferences.defaultFalse)

    }

    @Test
    fun testName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("customName"))

        sharedPreferences.named = true

        assertTrue(sharedPreferences.contains("customName"))

    }

    @Test
    fun testNameByPropertyName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("defaultTrue"))

        sharedPreferences.defaultTrue = true

        assertTrue(sharedPreferences.contains("defaultTrue"))

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

        sharedPreferences.optional = true

        assertTrue(sharedPreferences.contains("optional"))

        sharedPreferences.optional = null

        assertFalse(sharedPreferences.contains("optional"))

    }

}