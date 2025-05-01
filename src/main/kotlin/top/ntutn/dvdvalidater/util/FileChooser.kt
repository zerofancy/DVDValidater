package top.ntutn.dvdvalidater.util

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import top.ntutn.dvdvalidater.logger.slf4jLogger

object FileChooser {
    private val logger by slf4jLogger("file-chooser")

    suspend fun openFile(filterList: Set<String>? = null): String? {
        return kotlin.runCatching { openFileNative(filterList) }
            .onFailure { nativeException ->
                logger.error("A call to openFileNFD failed" , nativeException)
            }
            .getOrNull()
    }

    private suspend fun openFileNative(extensions: Set<String>?): String? {
        return FileKit.openFilePicker(type = FileKitType.File(extensions))?.file?.absolutePath
    }

    suspend fun chooseDirectory(): String? {
        return kotlin.runCatching { chooseDirectoryNative() }
            .onFailure { nativeException ->
                logger.error("A call to chooseDirectoryNative failed", nativeException)
            }
            .getOrNull()
    }

    private suspend fun chooseDirectoryNative(): String? {
        return FileKit.openDirectoryPicker()?.file?.absolutePath
    }

}