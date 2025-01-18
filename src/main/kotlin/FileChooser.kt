import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.nfd.NativeFileDialog
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

    private suspend fun openFileNFD(filterList: String) = withContext(Dispatchers.IO) {
        val pathPointer = MemoryUtil.memAllocPointer(1)
        try {
            return@withContext when (val code = NativeFileDialog.NFD_OpenDialog(filterList,"", pathPointer)) {
                NativeFileDialog.NFD_OKAY -> {
                    val path = pathPointer.stringUTF8
                    NativeFileDialog.nNFD_Free(pathPointer[0])

                    path
                }
                NativeFileDialog.NFD_CANCEL -> null
                NativeFileDialog.NFD_ERROR -> error("An error occurred while executing NativeFileDialog.NFD_OpenDialog")
                else -> error("Unknown return code '${code}' from NativeFileDialog.NFD_OpenDialog")
            }
        } finally {
            MemoryUtil.memFree(pathPointer)
        }
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

    private suspend fun chooseDirectoryNative() = withContext(Dispatchers.IO) {
        val pathPointer = MemoryUtil.memAllocPointer(1)
        try {
            return@withContext when (val code = NativeFileDialog.NFD_PickFolder("", pathPointer)) {
                NativeFileDialog.NFD_OKAY -> {
                    val path = pathPointer.stringUTF8
                    NativeFileDialog.nNFD_Free(pathPointer[0])

                    path
                }
                NativeFileDialog.NFD_CANCEL -> null
                NativeFileDialog.NFD_ERROR -> error("An error occurred while executing NativeFileDialog.NFD_PickFolder")
                else -> error("Unknown return code '${code}' from NativeFileDialog.NFD_PickFolder")
            }
        } finally {
            MemoryUtil.memFree(pathPointer)
        }
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