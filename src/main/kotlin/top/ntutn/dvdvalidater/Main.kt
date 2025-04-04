package top.ntutn.dvdvalidater

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import top.ntutn.dvdvalidater.generated.resources.Res
import top.ntutn.dvdvalidater.generated.resources.icon
import top.ntutn.dvdvalidater.ui.DigestButton
import top.ntutn.dvdvalidater.ui.ValidateButton
import top.ntutn.dvdvalidater.util.CrashAnalysisUtil
import top.ntutn.dvdvalidater.util.InstantDisplayLogger

@Composable
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            TextField(modifier = Modifier.weight(1f).fillMaxWidth(), value = InstantDisplayLogger.state.reversed().joinToString("\n"), onValueChange = {})
            Row {
                DigestButton()
                Spacer(Modifier.width(16.dp))
                ValidateButton()
                Spacer(Modifier.width(16.dp))
                Button(onClick = {
                    InstantDisplayLogger.state.clear()
                }) {
                    Text("Clear")
                }
            }
        }
    }
}


fun main() {
    application {
        CrashAnalysisUtil.plant()
        Window(title = "DVDValidater", onCloseRequest = ::exitApplication, icon = painterResource(Res.drawable.icon)) {
            App()
        }
    }
}
