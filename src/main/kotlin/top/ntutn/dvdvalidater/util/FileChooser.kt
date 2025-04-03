package top.ntutn.dvdvalidater.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser
import javax.swing.UIManager

private val logger = KotlinLogging.logger {}

object FileChooser {
    suspend fun openFile(filterList: String): String? {
        return kotlin.runCatching { openFileNFD(filterList) }
            .onFailure { nativeException ->
                logger.error(nativeException) { "A call to openFileNFD failed" }

                return kotlin.runCatching { openFileSwing() }
                    .onFailure { swingException ->
                        logger.error(swingException) { "A call to openFileSwing failed" }
                    }
                    .getOrNull()
            }
            .getOrNull()
    }

    private suspend fun openFileNFD(filterList: String): String {
        throw NotImplementedError("Consider open native dialog with jni/jna")
    }

    private suspend fun openFileSwing() = withContext(Dispatchers.IO) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        val chooser = JFileChooser("").apply {
            fileSelectionMode = JFileChooser.FILES_ONLY
            isVisible = true
        }

        when (val code = chooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> chooser.selectedFile.absolutePath
            JFileChooser.CANCEL_OPTION -> null
            JFileChooser.ERROR_OPTION -> error("An error occurred while executing JFileChooser::showOpenDialog")
            else -> error("Unknown return code '${code}' from JFileChooser::showOpenDialog")
        }
    }

    suspend fun chooseDirectory(): String? {
        return kotlin.runCatching { chooseDirectoryNative() }
            .onFailure { nativeException ->
                logger.error(nativeException) { "A call to chooseDirectoryNative failed" }

                return kotlin.runCatching { chooseDirectorySwing() }
                    .onFailure { swingException ->
                        logger.error(swingException) { "A call to chooseDirectorySwing failed" }
                    }
                    .getOrNull()
            }
            .getOrNull()
    }

    private suspend fun chooseDirectoryNative(): String {
        throw NotImplementedError("Consider open native dialog with jni/jna")
    }

    private suspend fun chooseDirectorySwing() = withContext(Dispatchers.IO) {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())

        val chooser = JFileChooser("").apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            isVisible = true
        }

        when (val code = chooser.showOpenDialog(null)) {
            JFileChooser.APPROVE_OPTION -> chooser.selectedFile.absolutePath
            JFileChooser.CANCEL_OPTION -> null
            JFileChooser.ERROR_OPTION -> error("An error occurred while executing JFileChooser::showOpenDialog")
            else -> error("Unknown return code '${code}' from JFileChooser::showOpenDialog")
        }
    }
}