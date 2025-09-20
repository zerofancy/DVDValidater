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
        private val sdf = SimpleDateFormat("HH:mm:ss")
        private val _logFlow = MutableStateFlow(emptyList<String>())
        val logFlow: Flow<List<String>> get() = _logFlow

        fun clear() {
            _logFlow.value = emptyList()
        }
    }

    private fun log(level: Level, message: String) {
        val time = synchronized(sdf) {
            sdf.format(System.currentTimeMillis())
        }
        val log = "$time [$level] $tag:$message"
        _logFlow.value += log
    }

    fun verbose(message: String) = log(Level.VERBOSE, message)

    fun debug(message: String) = log(Level.DEBUG, message)

    fun info(message: String) = log(Level.INFO, message)

    fun warn(message: String) = log(Level.WARN, message)

    fun error(message: String) = log(Level.ERROR, message)
}