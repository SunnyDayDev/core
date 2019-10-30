package dev.sunnyday.core.ui.retainingStore

import androidx.fragment.app.FragmentManager
import dev.sunnyday.core.runtime.create
import dev.sunnyday.core.runtime.tryOptional
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class RetainingStore internal constructor() {

    private val store = ConcurrentHashMap<Key<*>, Any>()

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

    companion object {

        fun instantiate(fm: FragmentManager): RetainingStore {

            val storeFragment = fm.findFragmentByTag(RetainingStoreFragment.TAG)
                    as? RetainingStoreFragment ?: create {
                        RetainingStoreFragment().also {
                            fm.beginTransaction()
                                .add(it, RetainingStoreFragment.TAG)
                                .commitNow()
                        }
                    }

            return storeFragment.store
        }

        fun checkInstantiated(fm: FragmentManager): Boolean =
            fm.findFragmentByTag(RetainingStoreFragment.TAG) != null

    }

}