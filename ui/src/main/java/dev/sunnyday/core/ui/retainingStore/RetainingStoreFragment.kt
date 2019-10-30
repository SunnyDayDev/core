package dev.sunnyday.core.ui.retainingStore

import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-01-31.
 * mail: mail@sunnydaydev.me
 */

internal class RetainingStoreFragment: Fragment() {

    internal companion object {

        const val TAG = "dev.sunnyday.core.ui.activity.retainstore.fragment"

    }

    internal val store = RetainingStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

}