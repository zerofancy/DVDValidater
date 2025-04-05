package top.ntutn.dvdvalidater.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.ntutn.dvdvalidater.logger.slf4jLogger
import javax.swing.JFileChooser
import javax.swing.UIManager

object FileChooser {
    private val logger by slf4jLogger("file-chooser")

    suspend fun openFile(filterList: String): String? {
        return kotlin.runCatching { openFileNFD(filterList) }
            .onFailure { nativeException ->
                logger.error("A call to openFileNFD failed" , nativeException)

                return kotlin.runCatching { openFileSwing() }
                    .onFailure { swingException ->
                        logger.error("A call to openFileSwing failed", swingException)
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
                logger.error("A call to chooseDirectoryNative failed", nativeException)

                return kotlin.runCatching { chooseDirectorySwing() }
                    .onFailure { swingException ->
                        logger.error("A call to chooseDirectorySwing failed", swingException)
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