package dev.sunnyday.core_uisample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dev.sunnyday.core.ui.activity.CoreActivity
import dev.sunnyday.core.util.intent

class MainActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.content, MainFragment())
            }
        }

        lifecycle

    }

    class MainFragment() : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.fragment_main, container, false)

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            view.findViewById<View>(R.id.mvvm).setOnClickListener { startActivity(intent<MVVMActivity>()) }
        }

    }

}
