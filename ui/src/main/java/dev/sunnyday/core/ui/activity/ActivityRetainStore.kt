package dev.sunnyday.core.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import dev.sunnyday.core.runtime.create
import dev.sunnyday.core.runtime.tryOptional

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

    operator fun <T: Any> set(clazz: Class<T>, value: T) {
        store[Key(clazz)] = value
    }

    operator fun <T: Any> set(clazz: Class<T>, tag: String, value: T) {
        store[Key(clazz, tag)] = value
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(clazz: Class<T>): T? = tryOptional { store[Key(clazz)] as? T }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(clazz: Class<T>, tag: String): T? = tryOptional { store[Key(clazz, tag)] as? T }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(clazz: Class<T>): T? = tryOptional { store.remove(Key(clazz)) as? T }

    @Suppress("UNCHECKED_CAST")
    fun <T: Any> remove(clazz: Class<T>, tag: String): T? = tryOptional { store.remove(Key(clazz, tag)) as? T }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T: Any> get() = get(T::class.java)

    inline fun <reified T: Any> get(tag: String) = get(T::class.java, tag)


    inline fun <reified T: Any> set(value: T) {
        this[T::class.java] = value
    }

    inline operator fun <reified T: Any> set(tag: String, value: T) {
        this[T::class.java, tag] = value
    }

    inline fun <reified T: Any> remove() = remove(T::class.java)

    inline fun <reified T: Any> remove(tag: String) = remove(T::class.java, tag)

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