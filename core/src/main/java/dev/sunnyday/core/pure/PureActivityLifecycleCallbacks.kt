package dev.sunnyday.core.pure

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
open class PureActivityLifecycleCallbacks: Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) { }

    override fun onActivityStarted(activity: Activity) { }

    override fun onActivityResumed(activity: Activity) { }
    
    override fun onActivityPaused(activity: Activity) { }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { }

    override fun onActivityStopped(activity: Activity) { }

    override fun onActivityDestroyed(activity: Activity) { }

}