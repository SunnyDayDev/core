package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

infix fun <T> T.alsoDo(@Suppress("UNUSED_PARAMETER") stub: Any): T = this

inline infix fun <T> T.alsoDo(action: () -> Any): T {
    action()
    return this
}

/**
 * When isn't exhaustive when used as a statement. This is a workaround that allows us to make sure
 * it is exhaustive.
 *
 * Do exhaustive when(sealedClass) {
 *  is A -> doSomething()
 *  is B -> doSomethingElse()
 * }
 */
@Suppress("NOTHING_TO_INLINE")
object Do {
    inline infix fun exhaustive(t: Any) = t
}