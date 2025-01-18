import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import org.slf4j.simple.SimpleLogger

private val logger = KotlinLogging.logger {  }

@Composable
fun App() {
    MaterialTheme {
        Column {
            DigestButton()
            ValidateButton()
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
                println(dir)
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
                println(digestFile)
                selectFileState = 0
            }
        }
    }
}

fun main() {
    application {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }
}
