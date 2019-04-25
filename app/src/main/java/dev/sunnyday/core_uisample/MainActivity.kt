package dev.sunnyday.core_uisample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import dev.sunnyday.core.util.AppGlobals
import dev.sunnyday.core.util.intent

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.mvvm).setOnClickListener { startActivity(intent<MVVMActivity>()) }

    }

}
