package top.ntutn.dvdvalidater.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import top.ntutn.dvdvalidater.Constants
import top.ntutn.dvdvalidater.generated.resources.Res
import top.ntutn.dvdvalidater.generated.resources.validate_button
import top.ntutn.dvdvalidater.logger.userLogger
import top.ntutn.dvdvalidater.util.DigestUtils
import top.ntutn.dvdvalidater.util.FileChooser
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

private val userLogger by userLogger(Constants.VALIDATE_BUTTON_LOG_TAG)

@Composable
fun ValidateButton(modifier: Modifier = Modifier) {
    var selectFileState by remember { mutableStateOf(0) }
    Button(modifier = modifier, enabled = selectFileState == 0, onClick = {
        selectFileState = 1
    }) {
        Text(stringResource(Res.string.validate_button))
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(selectFileState) {
        scope.launch {
            if (selectFileState == 1) {
                userLogger.info("选择摘要文件进行校验")
                val digestFile = FileChooser.openFile(setOf("dvdv"))
                userLogger.debug("selected $digestFile")
                if (digestFile != null) {
                    withContext(Dispatchers.IO) {
                        validateDigestFile(digestFile)
                    }
                }
                selectFileState = 0
            }
        }
    }
}

private fun validateDigestFile(checksumFilePath: String) {
    val file = File(checksumFilePath)
    if (!file.exists() || !file.canRead() || file.extension != Constants.CHECKSUM_FILE_EXTENSION) {
        userLogger.error("Not a valid checksum file")
        return
    }
    val df = DocumentBuilderFactory.newInstance()
    val document = df.newDocumentBuilder().parse(file)
    val rootElement = document.documentElement
    val checksumNodes = rootElement.getElementsByTagName(Constants.XML_CHECKSUM_ELEMENT)
    var passCount = 0
    var failedCount = 0
    repeat(checksumNodes.length) {
        val node = checksumNodes.item(it)
        val path = node.attributes.getNamedItem(Constants.XML_PATH_ATTRIBUTE).nodeValue
        val checksum = node.attributes.getNamedItem(Constants.XML_CHECKSUM_ATTRIBUTE).nodeValue
        val algorithm = node.attributes.getNamedItem(Constants.XML_ALGORITHM_ATTRIBUTE).nodeValue
        userLogger.debug("path=${path}, checksum=${checksum}, algorithm=${algorithm}")
        if (algorithm.lowercase() == Constants.XML_MD5_ALGORITHM) {
            val targetFile = File(file.parentFile, path)
            if (targetFile.canRead() && DigestUtils.getFileMD5(targetFile.canonicalPath) == checksum) {
                userLogger.debug("$path [$algorithm]$checksum pass")
                passCount++
            } else {
                userLogger.error("$path [$algorithm]$checksum failed")
                failedCount++
            }
        } else {
            userLogger.warn("Unknown algorithm")
            failedCount++
        }
    }
    userLogger.info("$passCount pass, $failedCount fail")
}