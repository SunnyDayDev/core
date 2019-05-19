package dev.sunnyday.core.mvvm.observable

import androidx.databinding.Bindable
import dev.sunnyday.core.mvvm.util.addOnPropertyChangedCallback
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel
import org.junit.Before
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-05-19.
 * mail: mail@sunnydaydev.me
 */

class BindableDelegateKtTest {

    companion object {

        private const val ID_VALUE = 1001
        private const val CUSTOM_ID_VALUE = -1001

    }

    @Before
    fun prepare() {

        Bindables.fieldsMap = mapOf(
            "value" to ID_VALUE
        )

    }

    @Test
    fun testBindableNotifyChanges() {

        val tester = InitialFalse()

        var nofitied = false
        tester.addOnPropertyChangedCallback { _, id ->
            when(id) {
                ID_VALUE -> nofitied = true
            }
        }

        tester.value = true

        assert(nofitied)

    }

    @Test
    fun testBindableByDefaultNotifyOnlyIfChanged() {

        val tester = InitialFalse()

        var nofitied = false
        tester.addOnPropertyChangedCallback { _, id ->
            when(id) {
                ID_VALUE -> nofitied = true
            }
        }

        tester.value = false

        assert(!nofitied)

    }

    @Test
    fun testBindableNotifyChangesAlwaysIfSet() {

        val tester = InitialFalseNotifyAlways()

        var nofitied = false
        tester.addOnPropertyChangedCallback { _, id ->
            when(id) {
                ID_VALUE -> nofitied = true
            }
        }

        tester.value = false

        assert(nofitied)

    }

    @Test
    fun testBindableNotifyCustomIdIfSet() {

        val tester = CustomId()

        var nofitied = false
        tester.addOnPropertyChangedCallback { _, id ->
            when(id) {
                CUSTOM_ID_VALUE -> nofitied = true
            }
        }

        tester.value = false

        assert(nofitied)

    }

    class InitialFalse: MVVMViewModel() {

        @get:Bindable var value by bindable(false)

    }

    class InitialFalseNotifyAlways: MVVMViewModel() {

        @get:Bindable var value by bindable(false, notifyOnlyIfChanged = false)

    }

    class CustomId: MVVMViewModel() {

        @get:Bindable var value by bindable(false, id = CUSTOM_ID_VALUE, notifyOnlyIfChanged = false)

    }

}