package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.service.PhoTransferService
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.ui.component.LabelSwitch
import io.github.takusan23.photransfer.ui.component.ServerFolderPathInfo
import io.github.takusan23.photransfer.ui.component.ServerInfo
import kotlinx.coroutines.launch

/**
 * サーバー画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerHomeScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)
    // 実行中？
    val isRunning = dataStore.value?.get(SettingKeyObject.IS_RUNNING) ?: false
    // PhoTransferサーバー情報
    val serverName = dataStore.value?.get(SettingKeyObject.SERVER_SIDE_DEVICE_NAME)

    // サービス起動
    LaunchedEffect(key1 = isRunning, block = {
        if (isRunning) {
            PhoTransferService.startService(context)
        } else {
            PhoTransferService.stopService(context)
        }
    })

    Scaffold(
        topBar = {
            MediumTopAppBar(title = { Text(text = stringResource(id = R.string.server_home_title)) })
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                    // 有効、無効スイッチ
                    ServerEnableSwitch(
                        isRunning = isRunning,
                        setting = context.dataStore
                    )

                    Spacer(modifier = Modifier.padding(top = 20.dp))

                    // サーバー情報
                    if (isRunning && serverName != null) {
                        ServerInfo(name = serverName)
                    }

                    // 保存先
                    ServerFolderPathInfo()

                }
            }
        }
    )

}

/**
 * 有効、無効スイッチ
 * @param isRunning 有効時true
 * @param setting DataStore
 * */
@Composable
private fun ServerEnableSwitch(isRunning: Boolean, setting: DataStore<Preferences>) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LabelSwitch(
        text = if (isRunning) stringResource(id = R.string.running_server) else stringResource(id = R.string.disable_title),
        isEnable = isRunning,
        onValueChange = { value ->
            scope.launch {
                setting.edit { it[SettingKeyObject.IS_RUNNING] = value }
                if (value) PhoTransferService.startService(context) else PhoTransferService.stopService(context)
            }
        }
    )
}