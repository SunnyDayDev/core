package dev.sunnyday.core.mvvm.binding

import android.os.Handler
import android.os.Looper
import android.view.View

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnyday.dev
 */
 
abstract class BindableCore<V: View, C: BindableCore.Change>(protected val view: V) {

    private val uiHandler = Handler(Looper.getMainLooper())

    private val pendingChanges = mutableSetOf<C>()

    private var currentPendingApply: Runnable? = null

    protected fun notifyChanges(vararg changes: C, post: Boolean = true) {

        pendingChanges.addAll(changes)

        currentPendingApply?.let(uiHandler::removeCallbacks)
        currentPendingApply = null

        if (post) {

            val pendingApply = Runnable { notifyChanges(post = false) }
                .also(this::currentPendingApply::set)

            uiHandler.post(pendingApply)

            return

        }

        val currentChanges = pendingChanges.toSet()
        pendingChanges.clear()

        applyChanges(currentChanges.sortedBy { it.applyingOrder })

    }

    protected abstract fun applyChanges(changes: List<C>)

    interface Change {

        val applyingOrder: Int

        object Simple: Change {

            override val applyingOrder: Int = 0

        }

    }

}