package dev.sunnyday.core.propertydelegate

import org.junit.Test

import org.junit.Assert.*

class DelegateToKtTest {

    @Test
    fun delegateToVal() {
        val valueForRaw = Any()
        testVal(ValDelegateTester(valueForRaw), valueForRaw)
    }

    @Test
    fun delegateToValHolder() {
        val valueForRaw = Any()
        testVal(ValDelegateToHolderTester(valueForRaw), valueForRaw)
    }

    @Test
    fun delegateToVar() {
        testVar(VarDelegateTester())
    }

    @Test
    fun delegateToVarHolder() {
        testVar(VarDelegateToHolderTester())
    }

    private fun testVal(tester: ValTester<Any>, expectedValue: Any) {
        assertEquals(tester.raw, expectedValue)
        assertEquals(tester.delegated, expectedValue)
    }

    private fun testVar(tester: VarTester<Any>) {
        val valueForRaw = Any()
        tester.raw = valueForRaw
        assertEquals(tester.raw, valueForRaw)
        assertEquals(tester.delegated, valueForRaw)

        val valueForDelegate = Any()
        tester.delegated = valueForDelegate
        assertEquals(tester.delegated, valueForDelegate)
        assertEquals(tester.raw, valueForDelegate)
    }

    private interface ValTester<T: Any> {

        val raw: T?

        val delegated: T?

    }

    private interface VarTester<T: Any> : ValTester<T> {

        override var raw: T?

        override var delegated: T?

    }

    private class ValDelegateTester<T: Any>(override val raw: T) : ValTester<T> {

        override val delegated by delegateTo(::raw)

    }

    private class VarDelegateTester<T: Any> : VarTester<T> {

        override var raw: T? = null

        override var delegated by delegateTo(::raw)

    }

    private class ValDelegateToHolderTester<T: Any>(value: T) : ValTester<T> {

        private val holder = Holder(value)

        override val raw: T
            get() = holder.raw

        override val delegated by delegateTo(holder::raw)

        inner class Holder(val raw: T)

    }

    private class VarDelegateToHolderTester<T: Any> : VarTester<T> {

        private val holder = Holder()

        override var raw: T?
            get() = holder.raw
            set(value) { holder.raw = value }

        override var delegated by delegateTo(holder::raw)

        inner class Holder {
            var raw: T? = null
        }

    }

}