package dev.sunnyday.core.util

import android.database.Cursor
import androidx.core.database.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnyday.dev
 */

fun Cursor.getBoolean(column: String): Boolean =
        getInt(getColumnIndexOrThrow(column)) != 0
fun Cursor.getBooleanOrNull(column: String): Boolean? =
        getIntOrNull(getColumnIndexOrThrow(column))?.let { it != 0 }

fun Cursor.getInt(column: String): Int =
        getInt(getColumnIndexOrThrow(column))
fun Cursor.getIntOrNull(column: String): Int? =
        getIntOrNull(getColumnIndexOrThrow(column))

fun Cursor.getLong(column: String): Long =
        getLong(getColumnIndexOrThrow(column))
fun Cursor.getLongOrNull(column: String): Long? =
        getLongOrNull(getColumnIndexOrThrow(column))

fun Cursor.getFloat(column: String): Float =
        getFloat(getColumnIndexOrThrow(column))
fun Cursor.getFloatOrNull(column: String): Float? =
        getFloatOrNull(getColumnIndexOrThrow(column))

fun Cursor.getDouble(column: String): Double =
        getDouble(getColumnIndexOrThrow(column))
fun Cursor.getDoubleOrNull(column: String): Double? =
        getDoubleOrNull(getColumnIndexOrThrow(column))

fun Cursor.getString(column: String): String =
        getString(getColumnIndexOrThrow(column))
fun Cursor.getStringOrNull(column: String): String? =
        getStringOrNull(getColumnIndexOrThrow(column))
