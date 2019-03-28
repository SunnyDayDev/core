package dev.sunnyday.core.mvvm.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import dev.sunnyday.core.mvvm.view.util.OnSelectionChangedListener

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 06.08.2018.
 * mail: mail@sunnyday.dev
 */
 
class MVVMEditText: AppCompatEditText, OnSelectionChangedListener.Owner {

    private var initialized = false
    private val selectionListeners = mutableSetOf<OnSelectionChangedListener>()
            .also { initialized = true }

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr)

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        if (!initialized) return

        selectionListeners.forEach {
            it.onSelectionChanged(this, selStart, selEnd)
        }

    }

    override fun addOnSelectionChangedListener(listener: OnSelectionChangedListener) {
        selectionListeners.add(listener)
    }

    override fun removeOnSelectionChangedListener(listener: OnSelectionChangedListener) {
        selectionListeners.remove(listener)
    }

}