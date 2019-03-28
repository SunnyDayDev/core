package dev.sunnyday.core.mvvm.util

import android.app.Activity
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding


/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnyday.dev
 */

fun <V: ViewDataBinding> Activity.setContentBinding(
    @LayoutRes layoutId: Int
): V = DataBindingUtil.setContentView(this, layoutId)