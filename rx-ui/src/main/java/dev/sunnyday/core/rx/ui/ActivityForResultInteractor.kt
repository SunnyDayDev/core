package dev.sunnyday.core.rx.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import dev.sunnyday.core.runtime.tryOptional
import dev.sunnyday.core.rx.invoke
import dev.sunnyday.core.ui.listener.OnActivityResultListener
import dev.sunnyday.core.util.Optional
import dev.sunnyday.core.util.equals
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import java.util.concurrent.TimeUnit

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface ActivityResultRequest<T> {

    val requestCode: Int
    val intentCreator: (Context) -> Intent
    val resultMapper: (Intent) -> T?

    companion object {

        fun <T> create(
            requestCode: Int,
            intentCreator: (Context) -> Intent,
            resultMapper: (Intent) -> T?
        ): ActivityResultRequest<T> = create(requestCode.toLong(), requestCode, intentCreator, resultMapper)

        fun <T> create(
            uniqueId: Long,
            requestCode: Int,
            intentCreator: (Context) -> Intent,
            resultMapper: (Intent) -> T?
        ): ActivityResultRequest<T> = Pure(uniqueId, requestCode, intentCreator, resultMapper)

    }

    private class Pure<T>(
        private val uniqueId: Long,
        override val requestCode: Int,
        override val intentCreator: (Context) -> Intent,
        override val resultMapper: (Intent) -> T?
    ): ActivityResultRequest<T> {

        override fun equals(other: Any?): Boolean = equals(this, other) {
            uniqueId == it.uniqueId
        }

        override fun hashCode(): Int = uniqueId.hashCode()

    }

}

interface ActivityForResultInteractor {

    fun <T> request(request: ActivityResultRequest<T>): Maybe<T>

    fun <T> checkHasResult(request: ActivityResultRequest<T>): Maybe<T>

}

class DefaultActivityForResultInteractor constructor(
        private val activityTracker: ActivityTracker
): ActivityForResultInteractor, OnActivityResultListener {

    private val activeRequests = mutableListOf<ActiveRequest<*>>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {

        val activeRequest = activeRequests.findLast { it.request.requestCode == requestCode } ?: return false

        activeRequest.result(Result(resultCode, data))

        return true

    }

    override fun <T> request(request: ActivityResultRequest<T>): Maybe<T> =
            activityTracker.lastStartedActivity
                    .filter { (activity) -> activity != null }
                    .firstOrError()
                    .timeout(500, TimeUnit.SECONDS, Single.just(Optional()))
                    .flatMapMaybe { (activity) ->

                        activity ?: error("No any activity started.")

                        activity.startActivityForResult(request.intentCreator(activity), request.requestCode)

                        val activeRequest = ActiveRequest(request)

                        activeRequests.add(activeRequest)

                        activeRequest.handle()

                    }

    override fun <T> checkHasResult(request: ActivityResultRequest<T>): Maybe<T> {

         val activeRequestWithResult = tryOptional {
             @Suppress("UNCHECKED_CAST")
             activeRequests.findLast {
                 it.request.requestCode == request.requestCode &&
                        it.result.hasValue()
             } as? ActiveRequest<T>
         } ?: return Maybe.empty()

        return activeRequestWithResult.handle()

    }

    private fun <T> ActiveRequest<T>.handle(): Maybe<T> {

        return result
            .doOnSuccess {
                val activeRequest = activeRequests.findLast { it.result === result } ?: return@doOnSuccess
                activeRequests.remove(activeRequest)
            }
            .filter { it.resultCode != Activity.RESULT_CANCELED }
            .flatMap {

                val mappedResult = request.resultMapper(it.data ?: Intent())

                if (mappedResult != null) Maybe.just(mappedResult)
                else Maybe.empty()

            }

    }

    private data class Result(val resultCode: Int, val data: Intent?)

    private class ActiveRequest<T>(val request: ActivityResultRequest<T>) {
        val result = SingleSubject.create<Result>()
    }

}

