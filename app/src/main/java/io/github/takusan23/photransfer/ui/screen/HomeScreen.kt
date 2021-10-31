package io.github.takusan23.photransfer.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.flow.first

/**
 * ホーム画面
 *
 * 初期設定が終わってなくてもまずこの画面を出す。この画面で判断する
 *
 * @param onNavigate 画面遷移してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
) {

    // DataStore読み出し
    val context = LocalContext.current
    val mode = remember { mutableStateOf("") }

    // 未設定時はnullにしてる
    LaunchedEffect(key1 = Unit, block = {
        val setting = context.dataStore.data.first()
        val settingModeValue = setting[SettingKeyObject.MODE]
        if (settingModeValue != null) {
            // 初期設定通過後
            mode.value = settingModeValue
        } else {
            // 初期設定まだしてない
            onNavigate(NavigationLinkList.SetupScreen)
        }
    })

    // それぞれの画面へ、初回起動時はセットアップ画面へ飛ばす
    when (mode.value) {
        SettingKeyObject.MODE_SERVER -> ServerHomeScreen(onNavigate = onNavigate)
        SettingKeyObject.MODE_CLIENT -> ClientHomeScreen(onNavigate = onNavigate)
    }

}