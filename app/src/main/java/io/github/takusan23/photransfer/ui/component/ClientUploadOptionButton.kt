package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.tool.WorkManagerTool
import java.text.SimpleDateFormat
import java.util.*

/**
 * 定期実行の設定とか今すぐ転送とか
 *
 * @param latestUploadDate 最後に転送した日時、ない場合はnullで
 * */
@Composable
fun ClientUploadOptionButton(
    latestUploadDate: Long? = null,
) {
    val context = LocalContext.current

    Column {
        HomeScreenItem(
            text = stringResource(id = R.string.client_upload_now),
            description = if (latestUploadDate != null) "${stringResource(id = R.string.latest_date)}：${toDateText(latestUploadDate)}" else null,
            icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
            onClick = { WorkManagerTool.oneShot(context) }
        )
    }
}

/**
 * 日付フォーマッター
 * @param time UnixTimeに1000かけた値
 * */
private fun toDateText(time: Long): String {
    return SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault()).format(time)
}