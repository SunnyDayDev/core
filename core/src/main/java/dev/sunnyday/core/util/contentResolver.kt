package dev.sunnyday.core.util

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import timber.log.Timber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-05.
 * mail: mail@sunnydaydev.me
 */

fun ContentResolver.query(uri: Uri,
                          projection: Array<String>? = null,
                          selection: String? = null,
                          selectionArgs: Array<String>? = null,
                          sortOrder: String? = null,
                          cancellationSignal: CancellationSignal? = null): Cursor? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal)
    } else {
        if (cancellationSignal != null) {
            Timber.d("Cancellation signal is specified but can't be used prior JellyBean.")
        }
        query(uri, projection, selection, selectionArgs, sortOrder)
    }
}