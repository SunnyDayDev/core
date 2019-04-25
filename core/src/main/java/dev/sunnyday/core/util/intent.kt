package dev.sunnyday.core.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle

/**
 * Created by sunny on 28.04.2018.
 * mail: mail@sunnyday.dev
 */

fun Intent.makeRestartActivityTask(): Intent {

    val result = Intent.makeRestartActivityTask(component)
    result.putExtras(this)
    return result

}

inline fun <reified T: Context> intent(pkg: String, config: Intent.() -> Unit = { }) : Intent = Intent()
    .setComponent(componentName(pkg, T::class.java))
    .apply(config)

inline fun <reified T: Context> intent(
        context: Context,
        config: Intent.() -> Unit = { }
) : Intent = intent<T>(context.packageName, config)

inline fun <reified T: Context> intent(config: Intent.() -> Unit = { }) : Intent =
    intent<T>(AppGlobals.applicationContext, config)

fun componentName(pkg: String, klass: Class<*>): ComponentName =
    ComponentName(pkg, klass.name)

fun componentName(context: Context, klass: Class<*>): ComponentName =
    ComponentName(context.packageName, klass.name)

fun componentName(klass: Class<*>): ComponentName =
    ComponentName(AppGlobals.applicationContext, klass.name)

inline fun Intent.withExtras(build: Bundle.() -> Unit): Intent = apply {
    val bundle = extras ?: Bundle()
    putExtras(bundle.apply(build))
}