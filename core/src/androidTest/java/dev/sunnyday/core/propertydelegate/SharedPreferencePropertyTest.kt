package dev.sunnyday.core.propertydelegate

import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before

open class SharedPreferencePropertyTest(private val name: String) {

    val prefs: SharedPreferences
        get() = InstrumentationRegistry.getInstrumentation()
        .context.getSharedPreferences(name, 0)

    @Before
    fun clearPrefs() {

        prefs.edit()
            .clear()
            .apply()

    }

}