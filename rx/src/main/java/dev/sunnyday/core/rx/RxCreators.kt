package dev.sunnyday.core.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
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