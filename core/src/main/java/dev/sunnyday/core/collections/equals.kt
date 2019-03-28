package dev.sunnyday.core.collections

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */


// TODO: escape using toTypedArray for better performance

inline infix fun <reified T> Collection<T>.contentDeepEquals(collection: Collection<T>?): Boolean {
    collection ?: return false
    return this.toTypedArray() contentDeepEquals collection.toTypedArray()
}

inline infix fun <reified T> Collection<T>.contentDeepNotEquals(collection: Collection<T>?): Boolean {
    return !(this contentDeepEquals collection)
}

inline infix fun <reified T> Collection<T>.contentEquals(collection: Collection<T>?): Boolean {
    collection ?: return false
    return this.toTypedArray() contentEquals collection.toTypedArray()
}

inline infix fun <reified T> Collection<T>.contentNotEquals(collection: Collection<T>?): Boolean {
    return !(this contentEquals  collection)
}

inline infix fun <reified T> Array<T>.contentNotEquals(array: Array<T>?): Boolean {
    array ?: return true
    return !(this contentEquals array)
}

inline infix fun <reified T> Array<T>.contentNotDeepEquals(array: Array<T>?): Boolean {
    array ?: return true
    return !(this contentDeepEquals array)
}
