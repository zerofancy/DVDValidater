package top.ntutn.dvdvalidater.logger

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.text.SimpleDateFormat

class UserLogger(private val tag: String) {
    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
    }

    companion object {
        private var log = StringBuffer()
        private val sdf = SimpleDateFormat("HH:mm:ss")
        private val _logFlow = MutableStateFlow("")
        val logFlow: Flow<String> get() = _logFlow

        fun clear() {
            log = StringBuffer()
            _logFlow.value = ""
        }
    }

    private fun log(level: Level, message: String) {
        val time = synchronized(sdf) {
            sdf.format(System.currentTimeMillis())
        }
        log.insert(0, "$time [$level] $tag:$message\n")
        _logFlow.value = log.toString()
    }

    fun verbose(message: String) = log(Level.VERBOSE, message)

    fun debug(message: String) = log(Level.DEBUG, message)

    fun info(message: String) = log(Level.INFO, message)

    fun warn(message: String) = log(Level.WARN, message)

    fun error(message: String) = log(Level.ERROR, message)
}