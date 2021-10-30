package io.github.takusan23.photransfer.ui.screen.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.ui.component.DeviceFindResult
import io.github.takusan23.photransfer.ui.component.DeviceFindText
import io.github.takusan23.photransfer.ui.screen.NavigationLinkList
import kotlinx.coroutines.launch

/**
 * クライアント設定画面。疎通確認だけ
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientSettingScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    // デバイスが発見できるか試す
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkServiceDiscovery = remember { NetworkServiceDiscovery(context).findDevice() }
    val findDevice = networkServiceDiscovery.collectAsState(initial = null)

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = stringResource(id = R.string.client_setting)) },
                navigationIcon = { IconButton(onClick = onBack, content = { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) }) }
            )
        },
        content = {
            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                if (findDevice.value != null) {
                    DeviceFindResult(findDevice.value!!)
                } else {
                    DeviceFindText()
                }
            }
        },
        floatingActionButton = {
            if (findDevice.value != null) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.start_client)) },
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_directions_run_24), contentDescription = null) },
                    onClick = {
                        // 設定書き込み
                        scope.launch {
                            context.dataStore.edit {
                                it[SettingKeyObject.IS_RUNNING] = true
                                it[SettingKeyObject.MODE] = SettingKeyObject.MODE_CLIENT
                            }
                            onNavigate(NavigationLinkList.HomeScreen)
                        }
                    }
                )
            }
        }
    )
}
