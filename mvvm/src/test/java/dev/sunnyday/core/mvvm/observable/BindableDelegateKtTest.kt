package dev.sunnyday.core.mvvm.observable

import androidx.databinding.Bindable
import dev.sunnyday.core.mvvm.BRTest
import dev.sunnyday.core.mvvm.viewModel.MVVMViewModel
import org.junit.BeforeClass
import org.junit.Test
import org.junit.Assert.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-05-19.
 * mail: mail@sunnydaydev.me
 */


class BindableDelegateKtTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun setup() {
            Bindables.bindingAdapterIdsClass = BRTest::class
        }

    }

    @Test
    fun `update of origin properly handled in the delegated field and notified`() {
        val originViewModel = OriginalViewModel()
        val delegatedViewModel = ReadOnlyDelegatedViewModel(originViewModel)

        assertEquals(0, delegatedViewModel.delegated)
        assertEquals(0, originViewModel.original)

        var isOriginNotified = false
        var isDelegatedNotified = false

        originViewModel.addOnPropertyChangedCallback(originViewModel::original) {
            isOriginNotified = true
        }

        delegatedViewModel.addOnPropertyChangedCallback(listOf(delegatedViewModel::delegated)) {
            isDelegatedNotified = true
        }

        originViewModel.original = 3


        assertEquals(3, delegatedViewModel.delegated)
        assertEquals(3, originViewModel.original)

        assertTrue(isOriginNotified)
        assertTrue(isDelegatedNotified)
    }

    @Test
    fun `set to the delegated properly update the origin and notify delegated and origin`() {
        val originViewModel = OriginalViewModel()
        val delegatedViewModel = ReadWriteDelegatedViewModel(originViewModel)

        assertEquals(0, delegatedViewModel.delegated)
        assertEquals(0, originViewModel.original)

        var isOriginNotified = false
        var isDelegatedNotified = false

        originViewModel.addOnPropertyChangedCallback(originViewModel::original) {
            isOriginNotified = true
        }

        delegatedViewModel.addOnPropertyChangedCallback(delegatedViewModel::delegated) {
            isDelegatedNotified = true
        }

        delegatedViewModel.delegated = 3

        assertEquals(3, originViewModel.original)
        assertEquals(3, delegatedViewModel.delegated)

        assertTrue(isOriginNotified)
        assertTrue(isDelegatedNotified)
    }

    private class OriginalViewModel: MVVMViewModel() {

        @get:Bindable
        var original by bindable(0)

    }

    private class ReadWriteDelegatedViewModel(originalViewModel: OriginalViewModel): MVVMViewModel() {

        @get:Bindable
        var delegated by originalViewModel.bindableDelegate { it::original }

    }

    private class ReadOnlyDelegatedViewModel(originalViewModel: OriginalViewModel): MVVMViewModel() {

        @get:Bindable
        val delegated by originalViewModel.bindableGetDelegate { it::original }

    }

}