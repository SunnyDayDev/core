package dev.sunnyday.core.mvi.spotify

import com.spotify.mobius.First
import com.spotify.mobius.MobiusLoop
import com.spotify.mobius.Next
import timber.log.Timber

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2018-12-27.
 * mail: mail@sunnydaydev.me
 */
 
class MobiusTimberLogger<M: Any, E: Any, F: Any>(
        private val tag: String,
        private val mapper: Mapper<M, E, F> = Mapper.Default()
): MobiusLoop.Logger<M, E, F> {

    interface Mapper<M: Any, E: Any, F: Any> {

        fun mapModel(model: M): String
        fun mapEvent(event: E): String
        fun mapEffect(effect: F): String

        open class Default<M: Any, E: Any, F: Any>: Mapper<M, E, F> {

            override fun mapModel(model: M): String = model::class.java.simpleName

            override fun mapEvent(event: E): String = event::class.java.simpleName

            override fun mapEffect(effect: F): String = effect::class.java.simpleName

        }

    }

    override fun beforeInit(model: M) {
        Timber.tag(tag)
        Timber.d("Initializing loop")
    }

    override fun afterInit(model: M, result: First<M, F>) {
        Timber.tag(tag)
        Timber.d("Loop initialized, starting from model: ${mapper.mapModel(result.model())}")

        for (effect in result.effects()) {
            Timber.tag(tag)
            Timber.d("Effect dispatched: ${mapper.mapEffect(effect)}")
        }
    }

    override fun exceptionDuringInit(model: M, exception: Throwable) {
        Timber.e(exception,
                "FATAL ERROR: exception during initialization from model '${mapper.mapModel(model)}'")
    }

    override fun beforeUpdate(model: M, event: E) {
        Timber.tag(tag)
        Timber.d("Event received: ${mapper.mapEvent(event)}")
    }

    override fun afterUpdate(model: M, event: E, result: Next<M, F>) {
        if (result.hasModel()) {
            Timber.tag(tag)
            Timber.d("Model updated: ${mapper.mapModel(result.modelUnsafe())}")
        }

        for (effect in result.effects()) {
            Timber.tag(tag)
            Timber.d("Effect dispatched: ${mapper.mapEffect(effect)}")
        }
    }

    override fun exceptionDuringUpdate(model: M, event: E, exception: Throwable) {
        Timber.e(exception,
                "FATAL ERROR: exception updating model '${mapper.mapModel(model)}' with event '${mapper.mapEvent(event)}'")
    }

}