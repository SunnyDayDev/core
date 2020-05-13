package dev.sunnyday.core.mvvm.observable

interface Notifiable {

    fun notifyChange()

    fun notifyPropertyChanged(propertyId: Int)

}