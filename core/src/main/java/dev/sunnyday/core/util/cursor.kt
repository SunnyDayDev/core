package dev.sunnyday.core.util

import android.database.Cursor
import androidx.core.database.*
import dev.sunnyday.core.runtime.ErrorResolverStrategy
import dev.sunnyday.core.runtime.resolve
import dev.sunnyday.core.runtime.tryOptional

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnyday.dev
 */

fun Cursor.getBoolean(column: String, errorResolver: ErrorResolverStrategy<Boolean>? = null): Boolean =
        get(errorResolver) { getInt(getColumnIndex(column)) != 0 }
fun Cursor.getBooleanOrNull(column: String, errorResolver: ErrorResolverStrategy<Boolean?>? = null): Boolean? =
        get(errorResolver) { getIntOrNull(getColumnIndex(column))?.let { it != 0 } }

fun Cursor.getInt(column: String, errorResolver: ErrorResolverStrategy<Int>? = null): Int =
        get(errorResolver) { getInt(getColumnIndex(column)) }
fun Cursor.getIntOrNull(column: String, errorResolver: ErrorResolverStrategy<Int?>? = null): Int? =
        get(errorResolver) { getIntOrNull(getColumnIndex(column)) }

fun Cursor.getLong(column: String, errorResolver: ErrorResolverStrategy<Long>? = null): Long =
        get(errorResolver) { getLong(getColumnIndex(column)) }
fun Cursor.getLongOrNull(column: String, errorResolver: ErrorResolverStrategy<Long?>? = null): Long? =
        get(errorResolver) { getLongOrNull(getColumnIndex(column)) }

fun Cursor.getFloat(column: String, errorResolver: ErrorResolverStrategy<Float>? = null): Float =
        get(errorResolver) { getFloat(getColumnIndex(column)) }
fun Cursor.getFloatOrNull(column: String, errorResolver: ErrorResolverStrategy<Float?>? = null): Float? =
        get(errorResolver) { getFloatOrNull(getColumnIndex(column)) }

fun Cursor.getDouble(column: String, errorResolver: ErrorResolverStrategy<Double>? = null): Double =
        get(errorResolver) { getDouble(getColumnIndex(column)) }
fun Cursor.getDoubleOrNull(column: String, errorResolver: ErrorResolverStrategy<Double?>? = null): Double? =
        get(errorResolver) { getDoubleOrNull(getColumnIndex(column)) }

fun Cursor.getString(column: String, errorResolver: ErrorResolverStrategy<String>? = null): String =
        get(errorResolver) { getString(getColumnIndex(column)) }
fun Cursor.getStringOrNull(column: String, errorResolver: ErrorResolverStrategy<String?>? = null): String? =
        get(errorResolver) { getStringOrNull(getColumnIndex(column)) }

private inline fun <T> Cursor.get(resolver: ErrorResolverStrategy<T>?, transformer: (Cursor) -> T): T {

    return try {
        transformer(this)
    } catch (error: Throwable) {
        resolver ?: throw error
        resolve(error, resolver)
    }

}