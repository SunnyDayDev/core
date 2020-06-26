package dev.sunnyday.core.rx.ui

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dev.sunnyday.core.ui.listener.OnRequestPermissionResultListener
import dev.sunnyday.core.util.Optional
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnyday.dev
 */

interface PermissionRequest {

    val requestCode: Int
    val permissions: Array<String>

    companion object {

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

interface RequestPermissionInteractor {

    fun requestPermissionIfNot(request: PermissionRequest): Completable

    fun checkPermission(request: PermissionRequest): Single<Boolean>

    fun waitPermissionCheck(request: PermissionRequest): Single<Boolean>

    fun checkShouldShowRationale(request: PermissionRequest): Single<Boolean>

}


sealed class RequestPermissionInteractorError: Error {

    constructor(): super()
    constructor(message: String): super(message)

    class NotGranted(vararg val permissions: String):
        RequestPermissionInteractorError("Permissions not granted: ${permissions.joinToString()}")

    class ActivityNotStarted: RequestPermissionInteractorError()

}

class DefaultRequestPermissionInteractor(
    private val context: Context,
    private val activityTracker: ActivityTracker
) : RequestPermissionInteractor, OnRequestPermissionResultListener {

    private val resultSubject: Subject<PermissionResultEvent> = PublishSubject.create()

    private val permissionCheckEvent: Subject<PermissionResultEvent> = PublishSubject.create()

    private val activeRequestPermissions = mutableListOf<Int>()


    override fun checkShouldShowRationale(request: PermissionRequest): Single<Boolean> =
        activityTracker.lastResumedActivity
            .firstOrError()
            .map { (activity) ->
                activity ?: throw RequestPermissionInteractorError.ActivityNotStarted()

                request.permissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
                }
            }

    override fun requestPermissionIfNot(request: PermissionRequest): Completable =
            checkPermission(request).flatMapCompletable { granted ->
                if (granted) Completable.complete()
                else activityTracker.lastResumedActivity
                        .filter { (activity) -> activity != null }
                        .firstElement()
                        .timeout(500, TimeUnit.SECONDS, Maybe.just(Optional()))
                        .flatMapCompletable { (activity) ->

                            activity ?: throw RequestPermissionInteractorError.ActivityNotStarted()

                            ActivityCompat.requestPermissions(
                                    activity,
                                    request.permissions,
                                    request.requestCode
                            )

                            activeRequestPermissions.add(request.requestCode)

                            resultSubject
                                    .filter { it.requestCode == request.requestCode }
                                    .firstOrError()
                                    .doOnSuccess(::checkPermissionResult)
                                    .ignoreElement()
                                    .doFinally { activeRequestPermissions.remove(request.requestCode) }

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
            Observable.merge(permissionCheckEvent, resultSubject)
                    .filter { request.requestCode == it.requestCode }
                    .firstOrError()
                    .flatMap(::checkPermission)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {

        val willHandled =  activeRequestPermissions.contains(requestCode)

        resultSubject.onNext(PermissionResultEvent(requestCode, permissions, grantResults))

        return willHandled

    }

    private fun checkPermissionResult(result: PermissionResultEvent) {

        val failed = result
                .grantResults.any { it != PackageManager.PERMISSION_GRANTED }

        if (failed) {

            val notGrantedPermissions = result.grantResults
                    .mapIndexed { index, r -> index to r }
                    .filterNot { (_, r) -> r == PackageManager.PERMISSION_GRANTED }
                    .map { (index, _) -> result.permissions[index] }

            throw RequestPermissionInteractorError.NotGranted(*notGrantedPermissions.toTypedArray())

        }

    }

    private fun checkPermission(result: PermissionResultEvent): Single<Boolean> =
            Completable.fromAction { checkPermissionResult(result) }
                    .toSingleDefault(true)
                    .onErrorReturnItem(false)

    private class PermissionResultEvent(val requestCode: Int,
                                        val permissions: Array<out String>,
                                        val grantResults: IntArray)

}