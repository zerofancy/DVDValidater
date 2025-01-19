import com.google.auto.service.AutoService
import org.slf4j.ILoggerFactory
import org.slf4j.IMarkerFactory
import org.slf4j.helpers.BasicMarkerFactory
import org.slf4j.helpers.NOPMDCAdapter
import org.slf4j.spi.MDCAdapter
import org.slf4j.spi.SLF4JServiceProvider

@AutoService(value = [SLF4JServiceProvider::class])
class InstantDisplayLogProvider: SLF4JServiceProvider {
    private val markerFactory by lazy {
        BasicMarkerFactory()
    }
    private val mdcFactory by lazy {
        NOPMDCAdapter()
    }

    override fun getLoggerFactory(): ILoggerFactory = ILoggerFactory {
        InstantDisplayLogger
    }

    override fun getMarkerFactory(): IMarkerFactory = markerFactory

    override fun getMDCAdapter(): MDCAdapter = mdcFactory

    override fun getRequestedApiVersion(): String = "2.0.99"

    override fun initialize() {
    }
}