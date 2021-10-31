package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.tool.WorkManagerTool
import io.github.takusan23.photransfer.ui.component.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen() {
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)
    val isRunning = dataStore.value?.get(SettingKeyObject.IS_RUNNING) ?: true
    // サーバー検索
    val serverFindFlow = remember { NetworkServiceDiscovery(context).findDevice() }
    val findServer = serverFindFlow.collectAsState(initial = null)

    // WorkManager（定期実行するやつ）起動
    LaunchedEffect(key1 = isRunning, block = {
        if (isRunning) {
            WorkManagerTool.registerRepeat(context, false)
        } else {
            WorkManagerTool.unRegisterRepeat(context)
        }
    })

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = stringResource(id = R.string.client_home_title)) })
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                    // 有効、無効スイッチ
                    ClientEnableSwitch(dataStore = dataStore.value)

                    Spacer(modifier = Modifier.padding(top = 20.dp))

                    if (findServer.value != null) {
                        // サーバー/転送先 情報
                        ClientServerInfo(nsdServiceInfo = findServer.value!!)
                        // 写真転送ボタン
                        ManualUploadButton(findServer = findServer.value!!)
                        Divider()
                        Spacer(modifier = Modifier.padding(top = 20.dp))
                        // 転送画面
                        ClientOneShotTransferButton(dataStore = dataStore.value)
                        // 充電中のみ転送
                        SettingTransferCharging(
                            dataStore = dataStore.value,
                            onValueChange = { isEnable ->
                                // 定期実行（WorkManager）再登録
                                WorkManagerTool.registerRepeat(context, isEnable)
                            }
                        )
                    } else {
                        ServerNotFoundInfo()
                    }

                }
            }
        }
    )

}

/**
 * クライアント実行ボタン（WorkManagerによる定期実行）
 *
 * @param dataStore DataStore
 * */
@Composable
fun ClientEnableSwitch(dataStore: Preferences?) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isRunning = dataStore?.get(SettingKeyObject.IS_RUNNING) ?: true

    LabelSwitch(
        text = if (isRunning) stringResource(id = R.string.running_client) else stringResource(id = R.string.client_disable_title),
        isEnable = isRunning,
        onValueChange = { isEnable ->
            scope.launch {
                context.dataStore.edit { it[SettingKeyObject.IS_RUNNING] = isEnable }
            }
        }
    )
}
