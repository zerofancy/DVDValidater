package top.ntutn.dvdvalidater.logger

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class UserLoggerDelegate(tag: String): ReadOnlyProperty<Any?, UserLogger> {
    private val logger = UserLogger(tag)

    override fun getValue(thisRef: Any?, property: KProperty<*>): UserLogger {
        return logger
    }
}

fun userLogger(tag: String) = UserLoggerDelegate(tag)

