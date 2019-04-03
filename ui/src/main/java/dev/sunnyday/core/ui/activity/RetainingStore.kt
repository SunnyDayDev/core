package dev.sunnyday.core.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.sunnyday.core.runtime.create
import dev.sunnyday.core.runtime.tryOptional
import kotlin.reflect.KClass

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

class RetainingStore internal constructor() {

    private val store = mutableMapOf<Key<*>, Any>()

    data class Key<T>(val clazz: Class<T>, val tag: String? = null)

    operator fun <T: Any> set(clazz: KClass<T>, value: T?) = set(Key(clazz.java), value)

    operator fun <T: Any> set(key: Key<T>, value: T?) {
        if (value != null) {
            store[key] = value
        } else {
            store.remove(key)
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(clazz: KClass<T>): T? = get(Key(clazz.java))

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(key: Key<T>): T? = tryOptional { store[key] as? T }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(clazz: KClass<T>): T? = remove(Key(clazz.java))

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(key: Key<T>): T? = tryOptional { store.remove(key) as? T }

    fun clear() {
        store.clear()
    }

}

val FragmentActivity.retainingStore: RetainingStore get() {

    val storeFragment = supportFragmentManager.findFragmentByTag(RetainingStoreFragment.TAG) as? RetainingStoreFragment
            ?: create {
                 RetainingStoreFragment().also {
                    supportFragmentManager.beginTransaction()
                        .add(it, RetainingStoreFragment.TAG)
                        .commitNow()
                }
            }

    return storeFragment.store

}

internal val FragmentActivity.isRetainingStoreInitialized: Boolean get() =
        supportFragmentManager.findFragmentByTag(RetainingStoreFragment.TAG) as? RetainingStoreFragment != null