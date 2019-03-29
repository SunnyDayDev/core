package dev.sunnyday.core.rx

import dev.sunnyday.core.util.Optional
import io.reactivex.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-29.
 * mail: mail@sunnydaydev.me
 */

// region: Observer

operator fun <T> Observer<T>.invoke(value: T) = onNext(value)
operator fun <T> Observer<T>.invoke(value: Throwable) = onError(value)

@JvmName("invokeOptional")
operator fun <T> Observer<Optional<T>>.invoke(value: T?) = onNext(Optional(value))

operator fun Observer<Unit>.invoke() = invoke(Unit)
fun <T> Observer<T>.notifier(value: T): Completable = Completable.fromAction { this(value) }
fun Observer<Unit>.notifier() = notifier(Unit)

operator fun <T> SingleObserver<T>.invoke(value: T) = onSuccess(value)
operator fun <T> SingleObserver<T>.invoke(value: Throwable) = onError(value)

operator fun <T> MaybeObserver<T>.invoke() = onComplete()
operator fun <T> MaybeObserver<T>.invoke(value: T) = onSuccess(value)
operator fun <T> MaybeObserver<T>.invoke(value: Throwable) = onError(value)

operator fun CompletableObserver.invoke() = onComplete()
operator fun CompletableObserver.invoke(error: Throwable) = onError(error)

// endregion

// region: Emitter

operator fun <T> ObservableEmitter<T>.invoke(throwable: Throwable) = tryOnError(throwable)
operator fun <T> FlowableEmitter<T>.invoke(throwable: Throwable) = tryOnError(throwable)

operator fun <T> Emitter<T>.invoke() = onComplete()
operator fun <T> Emitter<T>.invoke(value: T) = onNext(value)
operator fun <T> Emitter<T>.invoke(throwable: Throwable) = onError(throwable)

operator fun <T> SingleEmitter<T>.invoke(value: T) = onSuccess(value)
operator fun <T> SingleEmitter<T>.invoke(error: Throwable) = tryOnError(error)

operator fun <T> MaybeEmitter<T>.invoke() = onComplete()
operator fun <T> MaybeEmitter<T>.invoke(value: T) = onSuccess(value)
operator fun <T> MaybeEmitter<T>.invoke(error: Throwable) = tryOnError(error)

operator fun CompletableEmitter.invoke() = onComplete()
operator fun CompletableEmitter.invoke(error: Throwable) = tryOnError(error)

// endregion