package dev.sunnyday.core.util

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.CancellationSignal

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-05.
 * mail: mail@sunnydaydev.me
 */

@SuppressLint("Recycle")
fun ContentResolver.proceedQuery(uri: Uri,
                                 projection: Array<String>? = null,
                                 selection: String? = null,
                                 selectionArgs: Array<String>? = null,
                                 sortOrder: String? = null,
                                 cancellationSignal: CancellationSignal? = null): Cursor? =
    query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)

@SuppressLint("Recycle")
fun ContentResolver.proceedQuery(query: UriQuery): Cursor? = proceedQuery(query.uri, query.projection, query.selection, query.selectionArgs, query.sortOrder, query.cancellationSignal)

fun Uri.query(projection: Array<String>? = null,
              selection: String? = null,
              selectionArgs: Array<String>? = null,
              sortOrder: String? = null,
              cancellationSignal: CancellationSignal? = null): UriQuery? =
    UriQuery(this, projection, selection, selectionArgs, sortOrder, cancellationSignal)

data class UriQuery(
    val uri: Uri,
    val projection: Array<String>? = null,
    val selection: String? = null,
    val selectionArgs: Array<String>? = null,
    val sortOrder: String? = null,
    val cancellationSignal: CancellationSignal? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UriQuery

        if (uri != other.uri) return false
        if (projection != null) {
            if (other.projection == null) return false
            if (!projection.contentEquals(other.projection)) return false
        } else if (other.projection != null) return false
        if (selection != other.selection) return false
        if (selectionArgs != null) {
            if (other.selectionArgs == null) return false
            if (!selectionArgs.contentEquals(other.selectionArgs)) return false
        } else if (other.selectionArgs != null) return false
        if (sortOrder != other.sortOrder) return false
        if (cancellationSignal != other.cancellationSignal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uri.hashCode()
        result = 31 * result + (projection?.contentHashCode() ?: 0)
        result = 31 * result + (selection?.hashCode() ?: 0)
        result = 31 * result + (selectionArgs?.contentHashCode() ?: 0)
        result = 31 * result + (sortOrder?.hashCode() ?: 0)
        result = 31 * result + (cancellationSignal?.hashCode() ?: 0)
        return result
    }

}
