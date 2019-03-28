package dev.sunnyday.core.mvvm.view.util

import android.widget.EditText

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 19/10/2018.
 * mail: mail@sunnyday.dev
 */


interface OnSelectionChangedListener {

    fun onSelectionChanged(editText: EditText, start: Int, end: Int)

    interface Owner {

        fun addOnSelectionChangedListener(listener: OnSelectionChangedListener)

        fun removeOnSelectionChangedListener(listener: OnSelectionChangedListener)

    }

}