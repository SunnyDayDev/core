package dev.sunnyday.core.testUtils

import kotlin.properties.ReadWriteProperty

class ReadWritePropertyDelegateTester<T>(delegateCreator: () -> ReadWriteProperty<Any, T>) {

    var value: T by delegateCreator()

}