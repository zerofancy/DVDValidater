package top.ntutn.dvdvalidater.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Slf4jDelegate(tag: String): ReadOnlyProperty<Any?, Logger> {
    private val logger = LoggerFactory.getLogger(tag)

    override fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        return logger
    }
}

fun slf4jLogger(tag: String) = Slf4jDelegate(tag)

