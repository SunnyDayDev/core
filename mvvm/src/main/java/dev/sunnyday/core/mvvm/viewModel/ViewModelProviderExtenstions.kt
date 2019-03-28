package dev.sunnyday.core.mvvm.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.reflect.KClass

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnyday.dev
 */

inline operator fun <reified T: ViewModel> ViewModelProvider.invoke(): T = this[T::class]

operator fun <T: ViewModel> ViewModelProvider.get(clazz: KClass<T>): T = this[clazz.java]