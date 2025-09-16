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
import top.ntutn.dvdvalidater.generated.resources.Res
import top.ntutn.dvdvalidater.generated.resources.digest_button
import top.ntutn.dvdvalidater.logger.slf4jLogger
import top.ntutn.dvdvalidater.logger.userLogger
import top.ntutn.dvdvalidater.util.DigestUtils
import top.ntutn.dvdvalidater.util.FileChooser
import java.io.File
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.path.relativeTo

private val userLogger by userLogger("digest_button")
private val logger by slf4jLogger("digest_button")

@Composable
fun DigestButton(modifier: Modifier = Modifier) {
    var selectFileState by remember { mutableStateOf(0) }
    Button(modifier = modifier, enabled = selectFileState == 0, onClick = {
        selectFileState = 1
    }) {
        Text(stringResource(Res.string.digest_button))
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(selectFileState) {
        scope.launch {
            if (selectFileState == 1) {
                userLogger.info("选择文件夹生成摘要信息")
                val dir = FileChooser.chooseDirectory()
                userLogger.debug("choose $dir")
                if (!dir.isNullOrBlank()) {
                    withContext(Dispatchers.IO) {
                        generateDigestFile(dir)
                    }
                }
                selectFileState = 0
            }
        }
    }
}


private fun generateDigestFile(dirString: String) {
    val dirFile = File(dirString)
    if (!dirFile.exists() || !dirFile.isDirectory) {
        userLogger.error("目标位置不可用 $dirFile")
        return
    }
    val targetFile = File(dirFile, "checksum.dvdv")
    if (targetFile.exists()) {
        userLogger.warn("删除已存在的校验文件")
        targetFile.delete()
    }

    val dirPath = dirFile.toPath()

    val df = DocumentBuilderFactory.newInstance()
    val db = df.newDocumentBuilder()
    val document = db.newDocument()
    val rootElement = document.createElement("checksums")

    Files.walkFileTree(dirPath, object : FileVisitor<Path> {
        override fun preVisitDirectory(p0: Path?, p1: BasicFileAttributes): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

        override fun visitFile(p0: Path, p1: BasicFileAttributes): FileVisitResult {
            userLogger.debug("generating checksum for $p0")
            val mdString = DigestUtils.getFileMD5(p0.toString())
            userLogger.debug(mdString)

            val element = document.createElement("checksum")
            element.setAttribute("path", p0.relativeTo(dirPath).toString())
            element.setAttribute("checksum", mdString)
            element.setAttribute("algorithm", "md5")
            rootElement.appendChild(element)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(p0: Path?, p1: IOException): FileVisitResult {
            logger.warn("visit $p0 failed", p1)
            userLogger.warn("visit $p0 failed")
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(p0: Path?, p1: IOException?): FileVisitResult {
            return FileVisitResult.CONTINUE
        }

    })
    document.appendChild(rootElement)

    val tf = TransformerFactory.newInstance()
    val trans: Transformer = tf.newTransformer()
    trans.transform(DOMSource(document), StreamResult(targetFile.writer()))
}