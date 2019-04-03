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
 
private class RetainingStoreFragment: Fragment() {

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

    private val store = mutableMapOf<Key, Any>()

    private data class Key(val clazz: Class<out Any>, val tag: String? = null)

    operator fun <T: Any> set(clazz: KClass<T>, value: T) {
        store[Key(clazz.java)] = value
    }

    operator fun <T: Any> set(clazz: KClass<T>, tag: String, value: T) {
        store[Key(clazz.java, tag)] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(clazz: KClass<T>): T? = tryOptional { store[Key(clazz.java)] as? T }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(clazz: KClass<T>, tag: String): T? = tryOptional { store[Key(clazz.java, tag)] as? T }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(clazz: KClass<T>): T? = tryOptional { store.remove(Key(clazz.java)) as? T }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(clazz: KClass<T>, tag: String): T? = tryOptional { store.remove(Key(clazz.java, tag)) as? T }

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