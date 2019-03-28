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

inline fun <reified T: Context> createIntent(pkg: String, config: Intent.() -> Unit = { }) : Intent {
    return Intent()
            .setComponent(componentName(pkg, T::class.java))
            .apply(config)
}

inline fun <reified T: Context> createIntent(
        context: Context, config: Intent.() -> Unit = { }
) : Intent = createIntent<T>(context.packageName, config)

fun componentName(pkg: String, klass: Class<*>): ComponentName =
        ComponentName(pkg, klass.name)

inline fun Intent.withExtras(build: Bundle.() -> Unit): Intent {
    val bundle = extras ?: Bundle()
    putExtras(bundle.apply(build))
    return this
}