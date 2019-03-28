package dev.sunnyday.core.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnyday.dev
 */
 
@Suppress("unused")
sealed class Progressible<T> {

    val isDone get() = this is Done
    val isProgress get() = this is Progress
    val isUndefined get() = this is Undefined

    class Undefined<T>: Progressible<T>()

    data class Progress<T>(val progress: Long, val total: Long): Progressible<T>() {

        val ratio: Double = progress.toDouble() / total.toDouble()

        val percent: Int = (ratio * 100.0).toInt()

    }

    data class Done<T>(val value: T): Progressible<T>()

    companion object {

        inline fun <reified T> undefined(): Progressible<T> = Undefined()

        inline fun <reified T> progress(progress: Long, total: Long): Progressible<T> =
                Progress(progress, total)

        inline fun <reified T> done(value: T): Progressible<T> = Done(value)

    }

}