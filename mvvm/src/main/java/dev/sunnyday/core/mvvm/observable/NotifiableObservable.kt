package dev.sunnyday.core.mvvm.observable

import androidx.databinding.BaseObservable
import androidx.databinding.Observable

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnyday.dev
 */

interface NotifiableObservable :
    Observable,
    Notifiable

open class NotifiableBaseObservable : BaseObservable(), NotifiableObservable