package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen() {
    val context = LocalContext.current
    val discovery = remember { NetworkServiceDiscovery(context) }
    val findDevice = discovery.findDevice().collectAsState(initial = null)

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = "クライアント画面") })
        },
        content = {
            Text(
                modifier = Modifier.padding(5.dp),
                text = "${findDevice.value?.host}:${findDevice.value?.port}"
            )
        }
    )
}