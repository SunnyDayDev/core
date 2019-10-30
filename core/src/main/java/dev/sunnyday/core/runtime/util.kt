package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

inline fun <F: Any, S: Any, T> zipIfNonNil(f: F?, s: S?, zipper: (F, S) -> T): T? {
    val fNonNil = f ?: return null
    val sNonNil = s ?: return null
    return zipper(fNonNil, sNonNil)
}

inline fun <T> T.applyIf(predicate: (T) -> Boolean, action: T.() -> Unit): T =
    if (predicate(this)) apply(action) else this

inline fun <T> T.applyIf(predicate: Boolean, action: T.() -> Unit): T = applyIf({ predicate }, action)

inline fun <T> create(block: () -> T): T = block()

@Suppress("NOTHING_TO_INLINE")
inline fun noop() = Unit