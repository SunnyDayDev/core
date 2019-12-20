package dev.sunnyday.core.mvvm.binding

import android.graphics.Typeface
import android.text.method.MovementMethod
import androidx.databinding.BindingAdapter
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import dev.sunnyday.core.ui.source.StringSource

/**
 * Created by sunny on 04.05.2018.
 * mail: mail@sunnyday.dev
 */

object TextViewBindings: Bindings() {

    @JvmStatic
    @BindingAdapter("text")
    fun bindStringSourceText(view: TextView, source: StringSource) {
        view.text = source.get(view.context)
    }

    @JvmStatic
    @BindingAdapter("errorHint")
    fun bindErrorHint(view: TextView, error: String?) {
        view.error = error
    }

    @JvmStatic
    @BindingAdapter("onKeyEvent")
    fun bindOnKeyDown(view: TextView, listener: OnKeyListener?) {

        val viewListener: View.OnKeyListener? = listener?.let {
            View.OnKeyListener { _, keyCode, keyEvent ->
                it.onKey(keyCode, keyEvent)
            }
        }

        view.setOnKeyListener(viewListener)

    }

    @JvmStatic
    @BindingAdapter("movementMethod")
    fun bindMovementMethod(view: TextView, movementMethod: MovementMethod) {
        view.movementMethod = movementMethod
    }

    @JvmStatic
    @BindingAdapter(value = ["textStyle", "typeface"], requireAll = false)
    fun bindTextStyle(view: TextView, style: Int?, typeface: Typeface?) {

        val checkedStyle = style ?: view.typeface?.style ?: Typeface.NORMAL
        val checkedTypeface = Typeface.create(typeface ?: view.typeface, checkedStyle)

        view.setTypeface(checkedTypeface, checkedStyle)

    }

    @JvmStatic
    @BindingAdapter("onEditorAction")
    fun bindEditorActionListener(
        view: TextView,
        listener: OnEditorActionListener
    ) {

        view.setOnEditorActionListener { _, actionId, event ->

            listener.onEditorAction(actionId, event)

        }

    }

    @JvmStatic
    @BindingAdapter("onEditorAction")
    fun bindImeActionListener(
        view: TextView,
        listener: OnImeActionListener
    ) {

        view.setOnEditorActionListener { _, actionId, event ->

            when {

                event != null && !event.isShiftPressed -> {

                    val imeActionId =
                        if (actionId != 0) actionId
                        else view.imeOptions and EditorInfo.IME_MASK_ACTION

                    listener.onImeAction(imeActionId)

                }

                actionId != 0 -> listener.onImeAction(actionId)

                else -> false

            }

        }

    }

    interface OnEditorActionListener {
        fun onEditorAction(id: Int, event: KeyEvent?): Boolean
    }

    interface OnImeActionListener {
        fun onImeAction(id: Int): Boolean
    }

    interface OnKeyListener {
        fun onKey(keyCode: Int, event: KeyEvent): Boolean
    }

}