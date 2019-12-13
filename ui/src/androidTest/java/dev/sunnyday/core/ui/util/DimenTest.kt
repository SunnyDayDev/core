package dev.sunnyday.core.ui.util

import androidx.test.platform.app.InstrumentationRegistry
import dev.sunnyday.core.ui.test.R
import org.junit.Test

import org.junit.Assert.*

class DimenTest {

    private val context = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun dp() {
        assertEquals(Dimen.dp(16, context), context.resources.getDimension(R.dimen.dp16))
    }

    @Test
    fun sp() {
        assertEquals(Dimen.sp(16, context), context.resources.getDimension(R.dimen.sp16))
    }

}