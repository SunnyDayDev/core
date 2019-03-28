package dev.sunnyday.core.rx.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.sunnyday.core.rx.invoke
import dev.sunnyday.core.ui.listener.OnRequestPermissionResultListener
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnyday.dev
 */

interface PermissionRequest {

    val requestCode: Int
    val permissions: Array<String>

    object Factory {

        fun craete(requestCode: Int, permissions: Array<String>): PermissionRequest = Pure(requestCode, permissions)

    }

    private data class Pure(
        override val requestCode: Int,
        override val permissions: Array<String>
    ): PermissionRequest {

        override fun equals(other: Any?): Boolean {

            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Pure

            if (requestCode != other.requestCode) return false
            if (!permissions.contentEquals(other.permissions)) return false

            return true

        }

        override fun hashCode(): Int {

            var result = requestCode
            result = 31 * result + permissions.contentHashCode()
            return result

        }

    }

}

interface PermissionsInteractor {

    fun requestPermissionIfNot(request: PermissionRequest): Completable

    fun checkPermission(request: PermissionRequest): Single<Boolean>

    fun waitPermissionCheck(request: PermissionRequest): Single<Boolean>

    class ResultListener: OnRequestPermissionResultListener {

        private val resultSubject: Subject<PermissionResultEvent> = PublishSubject.create()

        internal val result = resultSubject.hide()

        override fun onRequestPermissionsResult(requestCode: Int,
                                       permissions: Array<out String>,
                                       grantResults: IntArray): Boolean {

            val result = PermissionResultEvent(requestCode, permissions, grantResults)

            resultSubject(result)

            return false

        }

    }

    object Factory {

        fun create(context: Context,
                   activityTracker: ActivityTracker,
                   resultListener: ResultListener
        ): PermissionsInteractor =
                PermissionsInteractorImpl(context, activityTracker, resultListener)

    }

}

internal class PermissionResultEvent(val requestCode: Int,
                                     val permissions: Array<out String>,
                                     val grantResults: IntArray)

class PermissionsNotGrantedError(vararg val permissions: String):
        Error("Permissions not granted: ${permissions.joinToString()}")

class PermissionsActivityNotStartedError: Error()

internal class PermissionsInteractorImpl(
    private val context: Context,
    private val activityTracker: ActivityTracker,
    private val resultListener: PermissionsInteractor.ResultListener
) : PermissionsInteractor {

    private val permissionCheckEvent: Subject<PermissionResultEvent> = PublishSubject.create()

    override fun requestPermissionIfNot(request: PermissionRequest): Completable =
            checkPermission(request).flatMapCompletable { granted ->
                if (granted) Completable.complete()
                else activityTracker.lastResumedActivity
                        .firstElement()
                        .flatMapCompletable { (activity) ->

                            activity ?: throw PermissionsActivityNotStartedError()

                            ActivityCompat.requestPermissions(
                                    activity,
                                    request.permissions,
                                    request.requestCode
                            )

                            resultListener.result
                                    .filter { it.requestCode == request.requestCode }
                                    .firstOrError()
                                    .doOnSuccess(::checkPermissionResult)
                                    .ignoreElement()

                        }
            }

    override fun checkPermission(request: PermissionRequest): Single<Boolean> =
            Single.fromCallable {

                val results = request.permissions
                        .map { ContextCompat.checkSelfPermission(context, it) }
                        .toIntArray()

                val result = PermissionResultEvent(
                        request.requestCode,
                        request.permissions,
                        results
                )

                result.also(permissionCheckEvent::onNext)

            } .flatMap(::checkPermission)

    override fun waitPermissionCheck(request: PermissionRequest): Single<Boolean> =
            Observable.merge(permissionCheckEvent, resultListener.result)
                    .filter { request.requestCode == it.requestCode }
                    .firstOrError()
                    .flatMap(::checkPermission)

    private fun checkPermissionResult(result: PermissionResultEvent) {

        val failed = result
                .grantResults.any { it != PackageManager.PERMISSION_GRANTED }

        if (failed) {

            val notGrantedPermissions = result.grantResults
                    .mapIndexed { index, r -> index to r }
                    .filterNot { (_, r) -> r == PackageManager.PERMISSION_GRANTED }
                    .map { (index, _) -> result.permissions[index] }

            throw PermissionsNotGrantedError(*notGrantedPermissions.toTypedArray())

        }

    }

    private fun checkPermission(result: PermissionResultEvent): Single<Boolean> =
            Completable.fromAction { checkPermissionResult(result) }
                    .toSingleDefault(true)
                    .onErrorReturnItem(false)

}