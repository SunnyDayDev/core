package dev.sunnyday.core.mvvm.viewModel

import androidx.databinding.Bindable
import androidx.databinding.Observable
import dev.sunnyday.core.mvvm.BRTest
import dev.sunnyday.core.mvvm.observable.Bindables
import dev.sunnyday.core.mvvm.observable.bindable
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test


class MVVMViewModelProjectionTest {

    companion object {

        @BeforeClass
        @JvmStatic
        fun setup() {
            Bindables.bindingAdapterIdsClass = BRTest::class
        }

    }

    private lateinit var root: TestRoot
    private lateinit var projection: TestProjection

    @Before
    fun prepareViewModels() {
        root = TestRoot()
        projection = TestProjection(root)
    }

    @Test
    fun `Should get same value from root`() {
        assertSame(root.value, projection.projectedValue)
        assertSame(root.variable, projection.projectedVariable)
    }

    @Test
    fun `Should write value to root and return the same`() {
        val test = "write test"
        assertEquals(root.variable, "variable")
        projection.projectedVariable = test

        assertSame(root.variable, test)
        assertSame(projection.projectedVariable, test)
    }

    @Test
    fun `Should notify projected value on root change`() {
        var notified = false

        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BRTest.projectedVariable) {
                    notified = true
                }
            }
        }

        projection.addOnPropertyChangedCallback(callback)

        root.variable = "notify test"

        assertTrue(notified)
    }

    @Test
    fun `Should notify root value on projection change`() {
        var notified = false

        val callback = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BRTest.variable) {
                    notified = true
                }
            }
        }

        root.addOnPropertyChangedCallback(callback)

        projection.projectedVariable = "notify test"

        assertTrue(notified)
    }

    class TestRoot : MVVMViewModel() {

        @get:Bindable
        val value: String by bindable("value")

        @get:Bindable
        var variable: String by bindable("variable")

    }

    class TestProjection(root: TestRoot) : MVVMViewModelProjection(root) {

        @get:Bindable
        var projectedVariable: String by rebindable(root::variable)
            .registerRebind(this::projectedVariable)

        @get:Bindable
        val projectedValue: String by rebindable(root::value)
            .registerRebind(this::projectedVariable)

    }

}