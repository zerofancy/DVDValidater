package top.ntutn.dvdvalidater.ui

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

private val logger = KotlinLogging.logger {  }

@Composable
fun DigestButton(modifier: Modifier = Modifier) {
    var selectFileState by remember { mutableStateOf(0) }
    Button(modifier = modifier, enabled = selectFileState == 0, onClick = {
        selectFileState = 1
    }) {
        Text("摘要")
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(selectFileState) {
        scope.launch {
            if (selectFileState == 1) {
                logger.info { "选择文件夹生成摘要信息" }
                val dir = FileChooser.chooseDirectory()
                logger.debug { "choose $dir" }
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
        logger.error { "目标位置不可用 $dirFile" }
        return
    }
    val targetFile = File(dirFile, "checksum.dvdv")
    if (targetFile.exists()) {
        logger.warn { "删除已存在的校验文件" }
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
            logger.debug { "generating checksum for $p0" }
            val mdString = DigestUtils.getFileMD5(p0.toString())
            logger.debug { mdString }

            val element = document.createElement("checksum")
            element.setAttribute("path", p0.relativeTo(dirPath).toString())
            element.setAttribute("checksum", mdString)
            element.setAttribute("algorithm", "md5")
            rootElement.appendChild(element)
            return FileVisitResult.CONTINUE
        }

        override fun visitFileFailed(p0: Path?, p1: IOException): FileVisitResult {
            logger.warn(p1) { "visit $p0 failed" }
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