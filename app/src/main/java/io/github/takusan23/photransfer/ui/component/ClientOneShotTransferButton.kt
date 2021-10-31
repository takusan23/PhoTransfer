package io.github.takusan23.photransfer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.tool.WorkManagerTool
import java.text.SimpleDateFormat
import java.util.*

/**
 * 今すぐ転送ボタン。こっちはWorkManager（定期実行で実行するタスク）を一度だけ実行する
 *
 * @param dataStore DataStore
 * */
@Composable
fun ClientOneShotTransferButton(dataStore: Preferences?) {
    val context = LocalContext.current
    // 最終転送日時
    val latestTransferDate = dataStore?.get(SettingKeyObject.CLIENT_LATEST_TRANSFER_DATE)

    HomeScreenItem(
        text = stringResource(id = R.string.client_upload_now),
        description = if (latestTransferDate != null) "${stringResource(id = R.string.latest_date)}：${toDateText(latestTransferDate)}" else null,
        icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
        onClick = { WorkManagerTool.oneShot(context) }
    )
}

/**
 * 日付フォーマッター
 * @param time UnixTimeに1000かけた値
 * */
private fun toDateText(time: Long): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(time)
}