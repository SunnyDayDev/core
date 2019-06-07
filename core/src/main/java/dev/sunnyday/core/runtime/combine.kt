package dev.sunnyday.core.runtime

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-07.
 * mail: mail@sunnydaydev.me
 */

inline fun <S, M, R> combine(
    crossinline first: (S) -> M,
    crossinline second: (M) -> R
): (S) -> R = {
    second(first(it))
}

inline fun <S, M1, M2, R> combine(
    crossinline first: (S) -> M1,
    crossinline second: (M1) -> M2,
    crossinline third: (M2) -> R
): (S) -> R = {
    third(second(first(it)))
}