package dev.sunnyday.core.ui.activity

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dev.sunnyday.core.runtime.alsoDo
import dev.sunnyday.core.ui.listener.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
open class CoreActivity: AppCompatActivity(),
    OnBackPressedListener.Registry.Owner,
    OnActivityResultListener.Registry.Owner,
    OnRequestPermissionResultListener.Registry.Owner {

    override val onBackPressedRegistry: OnBackPressedListener.Registry =
        DefaultOnBackPressedRegistry()

    override val onActivityResultRegistry: OnActivityResultListener.Registry =
        DefaultOnActivityResultRegistry()

    override val onRequestPermissionResultRegistry: OnRequestPermissionResultListener.Registry =
        DefaultOnRequestPermissionResultRegistry()


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!onActivityResultRegistry.onActivityResult(requestCode, resultCode, data)) {
            checkedOnActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!onRequestPermissionResultRegistry.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            checkedOnRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> true alsoDo onBackPressed()
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!onBackPressedRegistry.onBackPressed()) {
            checkedOnBackPressed()
        }
    }

    protected open fun checkedOnBackPressed() {
        super.onBackPressed()
    }

    protected open fun checkedOnRequestPermissionsResult(requestCode: Int,
                                                         permissions: Array<out String>,
                                                         grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    protected open fun checkedOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}