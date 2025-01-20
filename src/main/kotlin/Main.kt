import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.path.relativeTo


private val logger = KotlinLogging.logger {  }

@Composable
fun App() {
    MaterialTheme {
        Column {
            TextField(modifier = Modifier.weight(1f).fillMaxWidth(), value = InstantDisplayLogger.state.reversed().joinToString("\n"), onValueChange = {})
            Row {
                DigestButton()
                ValidateButton()
                Button(onClick = {InstantDisplayLogger.state.clear()}) {
                    Text("Clear")
                }
            }
        }
    }
}

@Composable
fun DigestButton(modifier: Modifier = Modifier) {
    var selectFileState by remember { mutableStateOf(0) }
    Button(enabled = selectFileState == 0, onClick = {
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

@Composable
fun ValidateButton(modifier: Modifier = Modifier) {
    var selectFileState by remember { mutableStateOf(0) }
    Button(enabled = selectFileState == 0, onClick = {
        selectFileState = 1
    }) {
        Text("校验")
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(selectFileState) {
        scope.launch {
            if (selectFileState == 1) {
                logger.info { "选择摘要文件进行校验" }
                val digestFile = FileChooser.openFile("")
                logger.debug { "selected $digestFile" }
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

private fun getFileMD5(filePath: String): String {
    val md = MessageDigest.getInstance("MD5")
    FileInputStream(File(filePath)).use { fis ->
        val dataBytes = ByteArray(1024)
        var nread: Int
        // 从文件输入流中读取数据，并更新MessageDigest对象
        while (fis.read(dataBytes).also { nread = it }!= -1) {
            md.update(dataBytes, 0, nread)
        }
    }
    // 完成MessageDigest操作
    val mdBytes = md.digest()
    // 将字节数组转换为十六进制字符串
    val sb = StringBuilder()
    for (mdByte in mdBytes) {
        sb.append(String.format("%02x", mdByte))
    }
    return sb.toString()
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
            val mdString = getFileMD5(p0.toString())
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

private fun validateDigestFile(checksumFilePath: String) {
    val file = File(checksumFilePath)
    if (!file.exists() || !file.canRead() || file.extension != "dvdv") {
        logger.error { "Not a valid checksum file" }
        return
    }
    val df = DocumentBuilderFactory.newInstance()
    val document = df.newDocumentBuilder().parse(file)
    val rootElement = document.documentElement
    val checksumNodes = rootElement.getElementsByTagName("checksum")
    var passCount = 0
    var failedCount = 0
    repeat(checksumNodes.length) {
        val node = checksumNodes.item(it)
        val path = node.attributes.getNamedItem("path").nodeValue
        val checksum = node.attributes.getNamedItem("checksum").nodeValue
        val algorithm = node.attributes.getNamedItem("algorithm").nodeValue
        logger.debug { "path=${path}, checksum=${checksum}, algorithm=${algorithm}" }
        if (algorithm.lowercase() == "md5") {
            val targetFile = File(file.parentFile, path)
            if (targetFile.canRead() && getFileMD5(targetFile.canonicalPath) == checksum) {
                logger.debug { "$path [$algorithm]$checksum pass" }
                passCount++
            } else {
                logger.error { "$path [$algorithm]$checksum failed" }
                failedCount++
            }
        } else {
            logger.warn { "Unknown algorithm" }
            failedCount++
        }
    }
    logger.info { "$passCount pass, $failedCount fail" }
}

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
