package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.alsoDo
import dev.sunnyday.core.runtime.tryOptional
import io.reactivex.Observable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-09.
 * mail: mail@sunnydaydev.me
 */

class RxDebugTest {

    @Test
    fun debugStackTrace() {

        tryOptional {
            Observable.fromCallable { Unit alsoDo error("Test") }
                .debug()
                .doOnError { it.printStackTrace() }
                .blockingSubscribe()
        }

        assertEquals(4, 2 + 2)

    }

}