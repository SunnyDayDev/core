package dev.sunnyday.core.ui.propertyDelegate

import android.os.Bundle
import android.os.Parcelable
import androidx.savedstate.SavedStateRegistryOwner
import dev.sunnyday.core.runtime.alsoDo
import java.lang.IllegalStateException
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T: Parcelable> restorable(
    defaultValue: () -> T
): ReadOnlyProperty<SavedStateRegistryOwner, T> = RestorableProperty(defaultValue)

private class RestorableProperty<T: Parcelable>(
    private val defaultValue: () -> T
) : ReadOnlyProperty<SavedStateRegistryOwner, T> {

    private var owner: SavedStateRegistryOwner? = null
    private var property: KProperty<*>?  = null

    private val unsafeTag: String get() =
        "RestorableProperty:${requireProperty().name}"

    private val lazyValue = lazy {
        restoreValue() alsoDo {
            registerSavedStateProvider()
            completeLazyInitialization()
        }
    }

    override fun getValue(thisRef: SavedStateRegistryOwner, property: KProperty<*>): T {
        if (!lazyValue.isInitialized()) {
            this.owner = thisRef
            this.property = property
        }

        return lazyValue.value
    }

    private fun registerSavedStateProvider() {
        val tag = this.unsafeTag

        requireOwner().savedStateRegistry.registerSavedStateProvider(tag) {
            Bundle().apply {
                putParcelable(tag, lazyValue.value)
            }
        }
    }

    private fun restoreValue(): T =
        requireOwner().savedStateRegistry.consumeRestoredStateForKey(unsafeTag)
            ?.getParcelable(unsafeTag)
            ?: defaultValue()

    private fun completeLazyInitialization() {
        owner = null
        property = null
    }

    private fun requireOwner(): SavedStateRegistryOwner =
        owner ?: throw IllegalStateException("Owner is null.")

    private fun requireProperty(): KProperty<*> =
        property ?: throw IllegalStateException("Property is null.")

}