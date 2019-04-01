package dev.sunnyday.core.dagger.extra

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

object GlobalComponentFactoryProviders {

    var activityComponentFactoryProvider: ActivityComponentFactoryProvider? = null

    var fragmentComponentFactoryProvider: FragmentComponentFactoryProvider? = null

}

// region Activity

interface ActivityComponentFactoryProvider {

    interface Owner {

        val activityComponentFactoryProvider: ActivityComponentFactoryProvider

    }

    fun <F: Any> get(activity: Activity, clazz: KClass<F>): F?

}

inline fun <reified F: Any> ActivityComponentFactoryProvider.get(activity: Activity): F? = 
    get(activity, F::class)

inline fun <reified F: Any> Activity.componentFactory(
    providerOwner: ActivityComponentFactoryProvider.Owner? = null
): F? {

    val clazz = F::class

    if (providerOwner != null) {
        return providerOwner.activityComponentFactoryProvider.get(this, clazz)
    }

    return (this as? ActivityComponentFactoryProvider.Owner)?.activityComponentFactoryProvider?.get(this, clazz)
        ?: (parent as? ActivityComponentFactoryProvider.Owner)?.activityComponentFactoryProvider?.get(this, clazz)
        ?: (application as? ActivityComponentFactoryProvider.Owner)?.activityComponentFactoryProvider?.get(this, clazz)
        ?: GlobalComponentFactoryProviders.activityComponentFactoryProvider?.get(this, clazz)
    
}

// endregion

// region Fragment

interface FragmentComponentFactoryProvider {

    interface Owner {

        val fragmentComponentFactoryProvider: FragmentComponentFactoryProvider

    }

    fun <F: Any> get(fragment: Fragment, clazz: KClass<F>): F?

}

inline fun <reified F: Any> FragmentComponentFactoryProvider.get(fragment: Fragment): F? = get(fragment, F::class)

inline fun <reified F: Any> Fragment.componentFactory(
    providerOwner: FragmentComponentFactoryProvider.Owner? = null
): F? {

    val clazz = F::class

    if (providerOwner != null) {
        return providerOwner.fragmentComponentFactoryProvider.get(this, clazz)
    }

    return (this as? FragmentComponentFactoryProvider.Owner)?.fragmentComponentFactoryProvider?.get(this, clazz)
        ?: (parentFragment as? FragmentComponentFactoryProvider.Owner)?.fragmentComponentFactoryProvider?.get(this, clazz)
        ?: (activity as? FragmentComponentFactoryProvider.Owner)?.fragmentComponentFactoryProvider?.get(this, clazz)
        ?: (context?.applicationContext as? FragmentComponentFactoryProvider.Owner)?.fragmentComponentFactoryProvider?.get(this, clazz)
        ?: GlobalComponentFactoryProviders.fragmentComponentFactoryProvider?.get(this, clazz)

}

// endregion