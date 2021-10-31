package io.github.takusan23.photransfer.ui.component

import android.os.Environment
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject

/**
 * サーバー情報を表示する
 *
 * @param dataStore DataStore
 * */
@Composable
fun ServerInfo(
    modifier: Modifier = Modifier,
    dataStore: Preferences,
) {
    val name = dataStore[SettingKeyObject.SERVER_SIDE_DEVICE_NAME]

    HomeScreenItem(
        modifier = Modifier.fillMaxWidth(),
        description = stringResource(id = R.string.device_name),
        text = name ?: "---",
        icon = painterResource(id = R.drawable.ic_outline_perm_device_information_24)
    )
}

/**
 * 保存先情報
 * */
@Composable
fun ServerFolderPathInfo(modifier: Modifier = Modifier) {
    HomeScreenItem(
        modifier = modifier.fillMaxWidth(),
        description = stringResource(id = R.string.save_folder),
        text = Environment.DIRECTORY_PICTURES,
        icon = painterResource(id = R.drawable.ic_outline_folder_24)
    )
}