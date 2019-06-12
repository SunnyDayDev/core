package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.currentStackTraceElement
import dev.sunnyday.core.util.Late
import io.reactivex.*
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.Maybes
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-03-20.
 * mail: mail@sunnyday.dev
 */

fun <T> Observables.combineLatest(sources: Iterable<Observable<T>>): Observable<List<T>> {

    if (sources.none()) return Observable.just(emptyList())

    return Observable.combineLatest(sources) { results ->
        results.map {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

}

fun <T> Singles.zip(sources: Iterable<Single<T>>): Single<List<T>> {

    if (sources.none()) return Single.just(emptyList())

    return Single.zip(sources) { results ->
        results.map {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

}

fun <T> Singles.concat(list: List<Single<T>>): Flowable<T> {
    return Single.concat(list)
}