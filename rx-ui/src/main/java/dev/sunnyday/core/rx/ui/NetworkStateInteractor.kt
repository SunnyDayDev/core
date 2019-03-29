package dev.sunnyday.core.rx.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import dev.sunnyday.core.rx.invoke
import dev.sunnyday.core.rx.mapToSignal
import io.reactivex.*
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 28.08.2018.
 * mail: mail@sunnyday.dev
 */

@SuppressLint("MissingPermission")
class NetworkStateInteractor(
        private val context: Context
) {

    private val connectivityManager by lazy<ConnectivityManager> {
        context.getSystemService() ?: error("Service not available.")
    }

    val networkAvailable: Single<Boolean>
    get() = Single.fromCallable {
        val activeNetwork = connectivityManager.activeNetworkInfo ?: return@fromCallable false
        activeNetwork.isConnected
    }

    fun waitConnection(): Completable = Completable.defer {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            waitConnectionLollipop()
        } else {
            waitConnectionPreLollipop()
        }

    }

    fun ifNetworkErrorWaitConnection(
        error: Throwable,
        waitHandler: Observer<Boolean>? = null
    ): Single<Unit> {

        return if (error.isNetworkError) {

            networkAvailable
                    .flatMap<Unit> { networkAvailable ->
                        if (networkAvailable) Single.error(error)
                        else waitConnectionOnError(waitHandler)
                    }

        } else {

            Single.error(error)

        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun waitConnectionLollipop(): Completable {

        val complete = CompletableSubject.create()

        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val callback = object: ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                complete()
            }

            override fun onUnavailable() {
                complete(IllegalStateException("Network not available."))
            }

        }

        connectivityManager.registerNetworkCallback(request, callback)

        return complete.doFinally { connectivityManager.unregisterNetworkCallback(callback) }

    }

    private fun waitConnectionPreLollipop(): Completable {

        val changed = PublishSubject.create<Unit>()

        val receiver = object: BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                changed()
            }

        }

        context.registerReceiver(
            receiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        )

        return changed
            .switchMapSingle { networkAvailable }
            .distinctUntilChanged()
            .mapToSignal(acceptableValue = true)
            .firstOrError()
            .ignoreElement()
            .doFinally { context.unregisterReceiver(receiver) }

    }

    private fun waitConnectionOnError(waitHandler: Observer<Boolean>?): Single<Unit> {

        val waitConnectionSource = waitConnection()
            .toSingleDefault(Unit)
            .doOnSubscribe { Timber.d("Network error on unavailable network. Wait for connection") }
            .doFinally { Timber.d("Network became available.") }

        return if (waitHandler == null) {

            waitConnectionSource

        } else {

            waitConnection()
                .toSingleDefault(Unit)
                .doOnSubscribe { waitHandler(true) }
                .doFinally {
                    waitHandler(false)
                    waitHandler.onComplete()
                }

        }

    }

    private val Throwable.isNetworkError: Boolean
        get() = this is UnknownHostException ||
                this is SocketTimeoutException ||
                cause?.isNetworkError == true

}