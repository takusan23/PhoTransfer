package io.github.takusan23.photransfer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.launch

/**
 * 充電中のみ転送を行う設定
 *
 * @param dataStore DataStore
 * @param onValueChange 値変化したとき呼ばれる。WorkManager再登録など
 * */
@Composable
fun SettingTransferCharging(
    dataStore: Preferences?,
    onValueChange: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 充電時のみ転送
    val isRequireCharging = dataStore?.get(SettingKeyObject.SETTING_TRANSFER_REQUIRE_CHARGING) ?: true

    HomeScreenSwitchItem(
        text = stringResource(id = R.string.setting_transfer_charging_only),
        isEnable = isRequireCharging,
        onValueChange = { isEnable ->
            scope.launch {
                context.dataStore.edit { it[SettingKeyObject.SETTING_TRANSFER_REQUIRE_CHARGING] = isEnable }
                onValueChange(isEnable)
            }
        },
        icon = painterResource(id = R.drawable.ic_outline_power_24)
    )
}