package dev.sunnyday.core.rx

import dev.sunnyday.core.util.Progressible
import io.reactivex.Observable

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 09.08.2018.
 * mail: mail@sunnyday.dev
 */

inline fun <S, reified R> Observable<Progressible<S>>.mapProgressible(noinline map: (S) -> R):
        Observable<Progressible<R>> = map {
    when(it) {
        is Progressible.Undefined -> Progressible.undefined()
        is Progressible.Progress -> Progressible.progress(it.progress, it.total)
        is Progressible.Done -> Progressible.done(map(it.value))
    }
}

fun <S> Observable<Progressible<S>>.doOnProgressDone(action: (S) -> Unit):
        Observable<Progressible<S>> = doOnNext {
    if (it is Progressible.Done) {
        action(it.value)
    }
}