package dev.sunnyday.core.ui.fragment

import android.content.Intent
import androidx.fragment.app.Fragment
import dev.sunnyday.core.ui.listener.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
open class CoreFragment: Fragment(),
    OnBackPressedListener.Registry.Owner,
    OnActivityResultListener.Registry.Owner,
    OnRequestPermissionResultListener.Registry.Owner,
    OnBackPressedListener {

    private val activityOnBackPressedListenerDelegate: OnBackPressedListener = object : OnBackPressedListener {

        override fun onBackPressed(): Boolean {
            if (isHidden) return false
            return this@CoreFragment.onBackPressed()
        }

    }

    override val onBackPressedRegistry: OnBackPressedListener.Registry =
        DefaultOnBackPressedRegistry()

    override val onActivityResultRegistry: OnActivityResultListener.Registry =
        DefaultOnActivityResultRegistry()

    override val onRequestPermissionResultRegistry: OnRequestPermissionResultListener.Registry =
        DefaultOnRequestPermissionResultRegistry()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultRegistry.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionResultRegistry.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStart() {
        super.onStart()

        (activity as? OnBackPressedListener.Registry.Owner)
            ?.onBackPressedRegistry
            ?.add(activityOnBackPressedListenerDelegate)

    }

    override fun onStop() {
        super.onStop()

        (activity as? OnBackPressedListener.Registry.Owner)
            ?.onBackPressedRegistry
            ?.remove(activityOnBackPressedListenerDelegate)

    }

    override fun onBackPressed() = false

}