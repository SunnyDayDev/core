package dev.sunnyday.core.collections

import java.util.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */
 
fun <T, K: Comparable<K>> sortedSetOf(vararg items: T, ascending: Boolean = true, sortKey: (T) -> K): TreeSet<T> =
        sortedSetOf(
                if (ascending) Comparator { f, s -> sortKey(f).compareTo(sortKey(s)) }
                else Comparator { f, s -> sortKey(s).compareTo(sortKey(f)) },
                *items
        )