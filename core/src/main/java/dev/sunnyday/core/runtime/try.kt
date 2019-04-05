package dev.sunnyday.core.runtime

import timber.log.Timber
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

inline fun <T> tryOptional(resolver: ErrorResolverStrategy<T?>? = null, action: () -> T): T? {

    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }

    return try {
        action()
    } catch (e: Throwable) {

        resolver ?: return null
        resolve(e, resolver)
    }

}

sealed class ErrorResolverStrategy<T> {

    abstract val log: Boolean

    data class Value<T>(val value: T, override val log: Boolean = false): ErrorResolverStrategy<T>()

    data class Rethrow<T>(override val log: Boolean = false): ErrorResolverStrategy<T>()

    data class Handle<T>(override val log: Boolean = false, val handle: (Throwable) -> T): ErrorResolverStrategy<T>()

}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> resolve(error: Throwable,
                       resolverStrategy: ErrorResolverStrategy<T>): T {

    if (resolverStrategy.log) {
        Timber.e(error)
    }

    return when(resolverStrategy) {
        is ErrorResolverStrategy.Value -> resolverStrategy.value
        is ErrorResolverStrategy.Handle -> resolverStrategy.handle(error)
        is ErrorResolverStrategy.Rethrow -> throw error
    }

}