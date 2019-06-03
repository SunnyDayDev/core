package dev.sunnyday.core.propertydelegate

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StringSetSharedPreferencesPropertyTest: SharedPreferencePropertyTest("stringSetTest") {

    var SharedPreferences.named: Set<String> by sharedPreferencesStringSet("customName", emptySet())
    var SharedPreferences.default: Set<String> by sharedPreferencesStringSet(default = setOf("3"))
    
    var SharedPreferences.optional: Set<String>? by sharedPreferencesStringSet()
    

    @Test
    fun testDefaultValue() {

        val sharedPreferences = prefs

        assertEquals(setOf("3"), sharedPreferences.default)

    }

    @Test
    fun testValueChanged() {

        val sharedPreferences = prefs

        sharedPreferences.default = setOf("5")

        assertEquals(setOf("5"), sharedPreferences.default)

    }

    @Test
    fun testName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("customName"))

        sharedPreferences.named = emptySet()

        assertTrue(sharedPreferences.contains("customName"))

    }

    @Test
    fun testNameByPropertyName() {

        val sharedPreferences = prefs

        assertFalse(sharedPreferences.contains("default"))

        sharedPreferences.default = emptySet()

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

        sharedPreferences.optional = emptySet()

        assertTrue(sharedPreferences.contains("optional"))

        sharedPreferences.optional = null

        assertFalse(sharedPreferences.contains("optional"))

    }

}