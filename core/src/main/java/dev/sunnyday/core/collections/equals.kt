package dev.sunnyday.core.collections

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-28.
 * mail: mail@sunnyday.dev
 */

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Collection<T>?.contentDeepEquals(other: Collection<T>?): Boolean =
    CollectionUtil.equals(this, other, ::contentDeepEqualsImpl)

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Collection<T>?.contentNotDeepEquals(collection: Collection<T>?): Boolean =
    !(this contentDeepEquals collection)

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Array<T>?.contentNotDeepEquals(array: Array<T>?): Boolean {

    if (this === array ) return true
    if (this == null || array == null) return false

    return !(this contentDeepEquals array)

}

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Collection<T>?.contentEquals(other: Collection<T>?): Boolean =
    CollectionUtil.equals(this, other)

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Collection<T>.contentNotEquals(collection: Collection<T>?): Boolean =
    !(this contentEquals  collection)

@Suppress("NOTHING_TO_INLINE")
inline infix fun <T> Array<T>?.contentNotEquals(array: Array<T>?): Boolean {

    if (this === array ) return true
    if (this == null || array == null) return false

    return !(this contentEquals array)

}

object CollectionUtil {

    inline fun <T> equals(
        first: Collection<T>?,
        second: Collection<T>?,
        equalsComparer: (T, T) -> Boolean = { f, s -> f == s }
    ): Boolean {

        if (first === second) return true
        if (first == null || second == null) return false
        if (first.size != second.size) return false

        val firstIterator = first.iterator()
        val secondIterator = second.iterator()

        while (firstIterator.hasNext()) {

            val firstNextItem = firstIterator.next()
            val secondNextItem = secondIterator.next()

            if (!(firstNextItem === secondNextItem || equalsComparer(firstNextItem, secondNextItem))) {
                return false
            }

        }

        return true

    }

}

@PublishedApi
internal fun <T> contentDeepEqualsImpl(v1: T, v2: T): Boolean {

    if (v1 === v2) return true
    if (v1 == null || v2 == null) return false
    if (v1 == v2) return true

    return when {

        v1 is Collection<*>&& v2 is Collection<*>-> v1.contentDeepEquals(v2)
        v1 is Array<*>     && v2 is Array<*>     -> v1.contentDeepEquals(v2)
        v1 is ByteArray    && v2 is ByteArray    -> v1.contentEquals(v2)
        v1 is ShortArray   && v2 is ShortArray   -> v1.contentEquals(v2)
        v1 is IntArray     && v2 is IntArray     -> v1.contentEquals(v2)
        v1 is LongArray    && v2 is LongArray    -> v1.contentEquals(v2)
        v1 is FloatArray   && v2 is FloatArray   -> v1.contentEquals(v2)
        v1 is DoubleArray  && v2 is DoubleArray  -> v1.contentEquals(v2)
        v1 is CharArray    && v2 is CharArray    -> v1.contentEquals(v2)
        v1 is BooleanArray && v2 is BooleanArray -> v1.contentEquals(v2)

        //v1 is UByteArray   && v2 is UByteArray   -> v1.contentEquals(v2)
        //v1 is UShortArray  && v2 is UShortArray  -> v1.contentEquals(v2)
        //v1 is UIntArray    && v2 is UIntArray    -> v1.contentEquals(v2)
        //v1 is ULongArray   && v2 is ULongArray   -> v1.contentEquals(v2)

        else -> false

    }

}