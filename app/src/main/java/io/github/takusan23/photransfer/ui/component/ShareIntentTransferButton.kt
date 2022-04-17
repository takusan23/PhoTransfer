package io.github.takusan23.photransfer.ui.component

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.data.ServerInfoData
import io.github.takusan23.photransfer.tool.PhoTransferClientTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 共有からアプリを開いたときの画面にある転送ボタン
 *
 * @param modifier Modifier
 * @param serverInfoData サーバー情報
 * @param onFinish 転送が終わったら呼ばれる。Toastが呼ばれる感じで
 * @param uriList 投稿する画像
 * */
@Composable
fun ShareIntentTransferButton(
    modifier: Modifier = Modifier,
    serverInfoData: ServerInfoData,
    uriList: List<Uri>,
    onFinish: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 進捗具合
    val progressPair = remember { mutableStateOf(0 to 0) }

    /** 転送を始める関数 */
    val startTransfer: () -> Unit = {
        // 一時的にコピーする。OkHttpがUri対応してないのでFile使えるようにしないといけない
        progressPair.value = uriList.size to 0
        scope.launch(Dispatchers.IO) {
            uriList.forEachIndexed { index, uri ->
                // 送信
                PhoTransferClientTool.uploadPhoto(
                    context,
                    uri,
                    serverInfoData.hostAddress,
                    serverInfoData.portNumber
                )
                progressPair.value = uriList.size to index + 1
            }
            // 完了
            withContext(Dispatchers.Main) {
                onFinish()
                Toast.makeText(context, context.getString(R.string.send_complete), Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (progressPair.value.first == progressPair.value.second) {
        // 転送ボタン
        Button(onClick = { startTransfer() }) {
            Icon(painter = painterResource(id = R.drawable.ic_outline_file_upload_24), contentDescription = null)
            Text(text = "${stringResource(id = R.string.transfer)} (${uriList.size})")
        }
    } else {
        // 転送中
        HomeScreenProgressItem(
            max = progressPair.value.first,
            current = progressPair.value.second,
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.manual_send_progress),
            description = "${progressPair.value.second} / ${progressPair.value.first}",
            icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
        )
    }
}