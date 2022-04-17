package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * クライアントホーム画面
 *
 * @param onNavigate 画面遷移してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)
    // サーバー検索
    val serverFindFlow = remember { NetworkServiceDiscovery(context).findServerOrGetLatestServer() }
    val serverInfo = serverFindFlow.collectAsState(initial = null)

    // WorkManager（定期実行するやつ）登録（もしくは解除）する。DataStoreを監視して
    LaunchedEffect(key1 = Unit, block = {
        context.dataStore.data.collect { preference ->

            val isRunning = preference[SettingKeyObject.IS_RUNNING] ?: false
            val isRequireCharging = preference[SettingKeyObject.SETTING_TRANSFER_REQUIRE_CHARGING] ?: true
            val intervalMinute = preference[SettingKeyObject.CLIENT_TRANSFER_INTERVAL_MINUTE] ?: SettingKeyObject.DEFAULT_CLIENT_TRANSFER_INTERVAL_MINUTE

            if (isRunning) {
                WorkManagerTool.registerRepeat(context, isRequireCharging, intervalMinute)
            } else {
                WorkManagerTool.unRegisterRepeat(context)
            }
        }
    })

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = stringResource(id = R.string.client_home_title)) })
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                    // 有効、無効スイッチ
                    ClientEnableSwitch(dataStore = dataStore.value)

                    Spacer(modifier = Modifier.padding(top = 20.dp))

                    if (serverInfo.value != null) {
                        // サーバー/転送先 情報
                        ClientServerInfo(serverInfoData = serverInfo.value!!)
                        // 写真転送ボタン
                        ManualUploadButton(serverInfoData = serverInfo.value!!)
                    } else {
                        ServerNotFoundInfo()
                    }

                    Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                    // 転送画面
                    ClientOneShotTransferButton(dataStore = dataStore.value)
                    // 充電中のみ転送
                    SettingTransferCharging(dataStore = dataStore.value)
                    // 定期実行の間隔
                    ClientTransferInterval(dataStore = dataStore.value)

                    Divider(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                    // このアプリについて
                    HomeScreenKonoAppButton(onClick = { onNavigate(NavigationLinkList.KonoAppScreen) })
                    // ライセンス
                    LicenseButton(onClick = { onNavigate(NavigationLinkList.LicenseScreen) })

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
    val isRunning = dataStore?.get(SettingKeyObject.IS_RUNNING) ?: false

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
