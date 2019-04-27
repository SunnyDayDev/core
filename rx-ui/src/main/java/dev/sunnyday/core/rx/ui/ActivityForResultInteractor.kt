package dev.sunnyday.core.rx.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import dev.sunnyday.core.ui.listener.OnActivityResultListener
import dev.sunnyday.core.util.equals
import io.reactivex.Maybe
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

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

    fun <T> checkUnhandled(request: ActivityResultRequest<T>): Maybe<T>

}

class DefaultActivityForResultInteractor constructor(
        private val activityTracker: ActivityTracker
): ActivityForResultInteractor, OnActivityResultListener {

    private val resultEventPublisher: Subject<Result> = PublishSubject.create()

    private val activeRequests = mutableListOf<Int>()

    private val unhandledResults = mutableSetOf<Result>()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {

        val willHandled = activeRequests.contains(requestCode)

        val result = Result(requestCode, resultCode, data)
        unhandledResults.add(result)
        resultEventPublisher.onNext(result)

        return willHandled

    }

    override fun <T> request(request: ActivityResultRequest<T>): Maybe<T> =
            activityTracker.lastStartedActivity
                    .firstOrError()
                    .flatMapMaybe { (activity) ->

                        activity ?: error("No any activity started.")

                        activity.startActivityForResult(request.intentCreator(activity), request.requestCode)

                        activeRequests.add(request.requestCode)

                        resultEventPublisher
                                .filter { it.requestCode == request.requestCode }
                                .firstElement()
                                .doOnSuccess { unhandledResults.remove(it) }
                                .filter { it.resultCode != Activity.RESULT_CANCELED }
                                .map { request.resultMapper(it.data ?: Intent()) }
                                .doFinally { activeRequests.remove(request.requestCode) }

                    }

    override fun <T> checkUnhandled(request: ActivityResultRequest<T>): Maybe<T> {

        val result = unhandledResults
            .find { it.requestCode == request.requestCode }
            ?.also { unhandledResults.remove(it) }
            ?.takeIf { it.resultCode != Activity.RESULT_CANCELED }
            ?: return Maybe.empty()

        return Maybe.just(request.resultMapper(result.data ?: Intent()))

    }

    private data class Result(val requestCode: Int, val resultCode: Int, val data: Intent?)

}

