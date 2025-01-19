import androidx.compose.runtime.mutableStateListOf
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.AbstractLogger
import org.slf4j.helpers.MessageFormatter
import java.text.SimpleDateFormat

object InstantDisplayLogger: AbstractLogger() {
    override fun readResolve(): Any = InstantDisplayLogger

    private val sdf = SimpleDateFormat()

    override fun isTraceEnabled(): Boolean = true

    override fun isTraceEnabled(p0: Marker?): Boolean = true

    override fun isDebugEnabled(): Boolean = true

    override fun isDebugEnabled(p0: Marker?): Boolean = true

    override fun isInfoEnabled(): Boolean = true

    override fun isInfoEnabled(p0: Marker?): Boolean = true

    override fun isWarnEnabled(): Boolean = true

    override fun isWarnEnabled(p0: Marker?): Boolean = true

    override fun isErrorEnabled(): Boolean = true

    override fun isErrorEnabled(p0: Marker?): Boolean = true

    override fun getFullyQualifiedCallerName(): String? = null

    override fun handleNormalizedLoggingCall(
        level: Level,
        marker: Marker?,
        messagePattern: String,
        arguments: Array<out Any>?,
        throwable: Throwable?
    ) {
        val formatted = MessageFormatter.basicArrayFormat(messagePattern, arguments)
        state.add("${sdf.format(System.currentTimeMillis())} [$level] $formatted")
        throwable?.stackTraceToString()?.let { state.add(it) }
    }

    val state = mutableStateListOf<String>()
}