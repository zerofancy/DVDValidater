package top.ntutn.dvdvalidater

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import top.ntutn.dvdvalidater.generated.resources.Res
import top.ntutn.dvdvalidater.generated.resources.clear_button
import top.ntutn.dvdvalidater.generated.resources.icon
import top.ntutn.dvdvalidater.generated.resources.title
import top.ntutn.dvdvalidater.logger.UserLogger
import top.ntutn.dvdvalidater.ui.DigestButton
import top.ntutn.dvdvalidater.ui.ValidateButton
import top.ntutn.dvdvalidater.util.CrashAnalysisUtil

@Composable
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            val userLog by UserLogger.logFlow.collectAsState(emptyList())
            val reversedLog by remember { derivedStateOf { userLog.reversed() } }
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    items(reversedLog.size) { i ->
                        SelectionContainer {
                            Text(reversedLog[i], modifier = Modifier
                                .background(if (i % 2 == 0 ) Color.Transparent else Color.LightGray)
                                .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Row {
                DigestButton()
                Spacer(Modifier.width(16.dp))
                ValidateButton()
                Spacer(Modifier.width(16.dp))
                Button(onClick = {
                    UserLogger.clear()
                }) {
                    Text(stringResource(Res.string.clear_button))
                }
            }
        }
    }
}


fun main() {
    application {
        CrashAnalysisUtil.plant()
        Window(title = stringResource(Res.string.title), onCloseRequest = ::exitApplication, icon = painterResource(Res.drawable.icon)) {
            App()
        }
    }
}
