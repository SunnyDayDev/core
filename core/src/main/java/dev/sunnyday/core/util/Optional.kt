package dev.sunnyday.core.util

/**
 * Created by sunny on 20.11.2017.
 * mail: mail@sunnyday.dev
 */

data class Optional<T>(var value: T? = null)

fun <T: Any> T?.toOptional(): Optional<T> = Optional(this)
