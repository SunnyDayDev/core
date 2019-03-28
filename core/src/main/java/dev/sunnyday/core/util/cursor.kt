package dev.sunnyday.core.util

import android.database.Cursor
import androidx.core.database.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 17.08.2018.
 * mail: mail@sunnyday.dev
 */

fun Cursor.getBoolean(column: String): Boolean = getInt(getColumnIndex(column)) != 0
fun Cursor.getBooleanOrNull(column: String): Boolean? = getIntOrNull(getColumnIndex(column))?.let { it != 0 }

fun Cursor.getInt(column: String): Int = getInt(getColumnIndex(column))
fun Cursor.getIntOrNull(column: String): Int? = getIntOrNull(getColumnIndex(column))

fun Cursor.getLong(column: String): Long = getLong(getColumnIndex(column))
fun Cursor.getLongOrNull(column: String): Long? = getLongOrNull(getColumnIndex(column))

fun Cursor.getFloat(column: String): Float = getFloat(getColumnIndex(column))
fun Cursor.getFloatOrNull(column: String): Float? = getFloatOrNull(getColumnIndex(column))

fun Cursor.getDouble(column: String): Double = getDouble(getColumnIndex(column))
fun Cursor.getDoubleOrNull(column: String): Double? = getDoubleOrNull(getColumnIndex(column))

fun Cursor.getString(column: String): String = getString(getColumnIndex(column))
fun Cursor.getStringOrNull(column: String): String? = getStringOrNull(getColumnIndex(column))