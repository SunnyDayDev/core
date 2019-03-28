package dev.sunnyday.core.util

import android.app.ActivityManager
import android.content.Context
import androidx.core.content.ContextCompat

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 31.07.2018.
 * mail: mail@sunnyday.dev
 */
 
inline fun <reified T> Context.getSystemService(): T =
        ContextCompat.getSystemService(this, T::class.java)
                ?: error("System service not available: ${T::class.java}")

@Suppress("MemberVisibilityCanBePrivate")
val Context.processName: String? get() {
    val pid = android.os.Process.myPid()
    return (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.runningAppProcesses
            ?.firstOrNull { it.pid == pid }
            ?.processName
}

val Context.isMainProcess get() = packageName == processName