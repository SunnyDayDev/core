package dev.sunnyday.core.rx

import dev.sunnyday.core.runtime.alsoDo
import dev.sunnyday.core.runtime.tryOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-09.
 * mail: mail@sunnydaydev.me
 */

class RxDebugTest {

    @Before
    fun init() {
        RxDebug.enabled = true
    }

    @Test
    fun debugStackTrace() {

        println("Start addition_isCorrect ")

        tryOptional {
            Observable.fromCallable { Unit alsoDo error("Test") }
                .debug()
                .doOnError { it.printStackTrace() }
                .blockingSubscribe()
        }

        println("Next case of addition_isCorrect")

        tryOptional {
            Observable.fromCallable { Unit alsoDo error("Test") }
                .debug("tag")
                .doOnError { it.printStackTrace() }
                .blockingSubscribe()
        }
        
        println("Next case of addition_isCorrect")

        tryOptional {
            Observable.fromCallable { Unit alsoDo error("Test") }
                .debug("tag") { "$it" }
                .doOnError { it.printStackTrace() }
                .blockingSubscribe()
        }

        println("Finish addition_isCorrect")

        assertEquals(4, 2 + 2)

    }

}