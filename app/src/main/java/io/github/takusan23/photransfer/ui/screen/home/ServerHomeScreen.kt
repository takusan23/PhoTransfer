package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.datastore.preferences.core.Preferences
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.service.PhoTransferService
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.ui.component.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * サーバーホーム画面
 *
 * @param onNavigate 画面遷移してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerHomeScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)

    // サービス起動
    LaunchedEffect(key1 = Unit, block = {
        // Flowでサービス有効かどうかを監視しておく
        context.dataStore.data
            .map { it[SettingKeyObject.IS_RUNNING] ?: false }
            .collect { isRunning ->
                if (isRunning) {
                    PhoTransferService.startService(context)
                } else {
                    PhoTransferService.stopService(context)
                }
            }
    })

    Scaffold(
        topBar = {
            MediumTopAppBar(title = { Text(text = stringResource(id = R.string.server_home_title)) })
        },
        content = {
            Box(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {

                    // 有効、無効スイッチ
                    if (dataStore.value != null) {
                        ServerEnableSwitch(dataStore = dataStore.value!!)
                        Spacer(modifier = Modifier.padding(top = 20.dp))
                        ServerInfo(dataStore = dataStore.value!!)
                        // ブラウザ版クライアント
                        BrowserLinkInfo(dataStore = dataStore.value!!)
                        // 充電中のみ
                        SettingTransferCharging(dataStore = dataStore.value!!)
                    }

                    // 保存先
                    ServerFolderPathInfo()
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
 * 有効、無効スイッチ
 *
 * @param dataStore DataStore
 * */
@Composable
private fun ServerEnableSwitch(dataStore: Preferences) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 実行中？
    val isRunning = dataStore.get(SettingKeyObject.IS_RUNNING) ?: false

    LabelSwitch(
        text = if (isRunning) stringResource(id = R.string.running_server) else stringResource(id = R.string.disable_title),
        isEnable = isRunning,
        onValueChange = { value ->
            scope.launch {
                if (value) PhoTransferService.startService(context) else PhoTransferService.stopService(context)
            }
        }
    )
}