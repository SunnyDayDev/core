package dev.sunnyday.core.ui.listener

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import dev.sunnyday.core.pure.PureActivityLifecycleCallbacks

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
object ApplicationCoreListenersUtil {

    fun autoAttachListeners(application: Application,
                            onBackPressedListener: OnBackPressedListener.Registry.Owner?,
                            onActivityResultListener: OnActivityResultListener.Registry.Owner?,
                            onRequestPermissionResultListener: OnRequestPermissionResultListener.Registry.Owner?,
                            attachToNestedFragments: Boolean = true) {

        autoAttachListeners(
            application,
            onBackPressedListener?.onBackPressedRegistry,
            onActivityResultListener?.onActivityResultRegistry,
            onRequestPermissionResultListener?.onRequestPermissionResultRegistry,
            attachToNestedFragments
        )

    }

    fun autoAttachListeners(application: Application,
                       onBackPressedListener: OnBackPressedListener?,
                       onActivityResultListener: OnActivityResultListener?,
                       onRequestPermissionResultListener: OnRequestPermissionResultListener?,
                       attachToNestedFragments: Boolean = true) {

        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks by PureActivityLifecycleCallbacks() {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                attachToTarget(onBackPressedListener, onActivityResultListener, onRequestPermissionResultListener, activity)

                if (activity is FragmentActivity) {

                    activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {

                        override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
                            attachToTarget(onBackPressedListener, onActivityResultListener, onRequestPermissionResultListener, f)
                        }

                    }, attachToNestedFragments)

                }

            }

        })

    }
    
    private fun attachToTarget(onBackPressedListener: OnBackPressedListener?,
                       onActivityResultListener: OnActivityResultListener?,
                       onRequestPermissionResultListener: OnRequestPermissionResultListener?,
                       target: Any) {

        if (onBackPressedListener != null && target is OnBackPressedListener.Registry.Owner) {
            target.onBackPressedRegistry.add(onBackPressedListener)
        }

        if (onActivityResultListener != null && target is OnActivityResultListener.Registry.Owner) {
            target.onActivityResultRegistry.add(onActivityResultListener)
        }

        if (onRequestPermissionResultListener != null && target is OnRequestPermissionResultListener.Registry.Owner) {
            target.onRequestPermissionResultRegistry.add(onRequestPermissionResultListener)
        }
        
    }
    
}