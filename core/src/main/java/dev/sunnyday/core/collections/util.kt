package dev.sunnyday.core.collections

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

fun <E> List<E>.change(index: Int, item: E): List<E> = toMutableList().apply { set(index, item) }.toList()
fun <E> List<E>.change(entry: Pair<Int, E>): List<E> = change(entry.first, entry.second)

fun <E> List<E>.without(items: List<E>): List<E> = toMutableList().apply { removeAll(items) }.toList()
fun <E> Set<E>.without(items: List<E>): Set<E> = toMutableList().apply { removeAll(items) }.toSet()