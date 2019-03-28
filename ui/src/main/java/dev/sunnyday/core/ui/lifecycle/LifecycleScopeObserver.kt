package dev.sunnyday.core.ui.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import dev.sunnyday.core.runtime.alsoDo

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 10.08.2018.
 * mail: mail@sunnyday.dev
 */

internal class LifecycleScopeObserver(
    private val scope: Lifecycle.State,
    private val lifecycle: Lifecycle,
    private val onEnter: () -> Unit,
    private val onExit: () -> Unit
): LifecycleObserver {

    private var currentlyInScope = false

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    internal fun onAny() {

        val inScope = lifecycle.currentState.isAtLeast(scope)

        currentlyInScope = when {
            inScope && !currentlyInScope -> true alsoDo onEnter()
            !inScope && currentlyInScope -> false alsoDo onExit()
            else -> currentlyInScope
        }

    }

}

fun Lifecycle.addScopeObserver(
    scope: Lifecycle.State,
    onEnter: () -> Unit,
    onExit: () -> Unit
) {

    val observer = LifecycleScopeObserver(scope, this, onEnter, onExit)
    addObserver(observer)

}
