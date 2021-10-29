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

/**
 * サーバー画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerScreen() {
    val context = LocalContext.current
    val discovery = remember { NetworkServiceDiscovery(context) }
    val serviceName = discovery.registerService(4649).collectAsState(initial = null)

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = "サーバー起動") })
        },
        content = {
            Text(
                modifier = Modifier.padding(5.dp),
                text = serviceName.value ?: "不明"
            )
        }
    )

}