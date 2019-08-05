package dev.sunnyday.core_uisample

import android.os.Bundle
import android.view.View
import dev.sunnyday.core.ui.activity.CoreActivity
import dev.sunnyday.core.util.intent

class MainActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.mvvm).setOnClickListener { startActivity(intent<MVVMActivity>()) }

        lifecycle

    }

}
