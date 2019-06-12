package dev.sunnyday.core.rx

import dev.sunnyday.core.collections.contentDeepEquals
import io.reactivex.functions.BiPredicate

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-06-12.
 * mail: mail@sunnydaydev.me
 */
 
object Predicates {

    @JvmStatic
    fun <T: Collection<*>> contendDeepEquals(): BiPredicate<T, T> = BiPredicate { f, s ->
        f contentDeepEquals s
    }

}