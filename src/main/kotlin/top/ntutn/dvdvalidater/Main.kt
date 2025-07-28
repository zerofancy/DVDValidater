package top.ntutn.dvdvalidater

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import top.ntutn.dvdvalidater.generated.resources.Res
import top.ntutn.dvdvalidater.generated.resources.icon
import top.ntutn.dvdvalidater.logger.UserLogger
import top.ntutn.dvdvalidater.ui.DigestButton
import top.ntutn.dvdvalidater.ui.ValidateButton
import top.ntutn.dvdvalidater.util.CrashAnalysisUtil

@Composable
fun App() {
    MaterialTheme {
        Column(modifier = Modifier.padding(8.dp)) {
            val userLog by UserLogger.logFlow.collectAsState("")
            TextField(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                value = userLog,
                onValueChange = {}
            )
            Row {
                DigestButton()
                Spacer(Modifier.width(16.dp))
                ValidateButton()
                Spacer(Modifier.width(16.dp))
                Button(onClick = {
                    UserLogger.clear()
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
