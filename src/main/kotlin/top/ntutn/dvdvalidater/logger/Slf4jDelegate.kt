package top.ntutn.dvdvalidater.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Slf4jDelegate(private val tag: String): ReadOnlyProperty<Any?, Logger> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Logger {
        return LoggerFactory.getLogger(tag)
    }
}

fun slf4jLogger(tag: String) = Slf4jDelegate(tag)

