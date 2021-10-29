package io.github.takusan23.photransfer.ui.screen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.flow.collect

/**
 * ホーム画面
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigation: (String) -> Unit,
) {

    // DataStore読み出し
    val context = LocalContext.current
    val mode = remember { mutableStateOf("") }

    // 未設定時はnullにしてる
    LaunchedEffect(key1 = Unit, block = {
        context.dataStore.data.collect {
            val settingModeValue = it[SettingKeyObject.MODE]
            println(settingModeValue)
            if (settingModeValue != null) {
                // 初期設定通過後
                mode.value = settingModeValue
            } else {
                // 初期設定まだしてない
                onNavigation(NavigationLinkList.SetupScreen)
            }
        }
    })

    // それぞれの画面へ、初回起動時はセットアップ画面へ飛ばす
    when (mode.value) {
        SettingKeyObject.MODE_SERVER -> ServerHomeScreen()
        SettingKeyObject.MODE_CLIENT -> ClientHomeScreen()
    }

}