package dev.sunnyday.core_uisample

import android.os.Bundle
import android.view.View
import dev.sunnyday.core.propertydelegate.bundleBoolean
import dev.sunnyday.core.rx.deferShared
import dev.sunnyday.core.rx.debug
import dev.sunnyday.core.ui.activity.CoreActivity
import dev.sunnyday.core.util.intent
import io.reactivex.Single
import io.reactivex.rxkotlin.Singles

class MainActivity : CoreActivity() {

    companion object {

        private var Bundle.boolean: Boolean by bundleBoolean(default = false)
        private var Bundle.optionalBoolean: Boolean? by bundleBoolean()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.mvvm).setOnClickListener { startActivity(intent<MVVMActivity>()) }

        runInvisibleTests()

        lifecycle

    }

    private fun runInvisibleTests() {

        testSingesCachedWhileNotTerminated()

    }

    private fun testSingesCachedWhileNotTerminated() {

        Singles.deferShared(mutableMapOf()) {
            Single.fromCallable { true }
                .debug()
        }
            .debug()
            .subscribe()

    }

}
