package io.github.takusan23.photransfer.ui.component

import android.net.nsd.NsdServiceInfo
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.tool.PhoTransferClientTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 手動でローカルの写真を選んでアップロードする
 *
 * @param modifier Modifier
 * @param findServer 転送先の情報。NetworkServiceDiscovery#findServer()参照
 * */
@Composable
fun ManualUploadButton(modifier: Modifier = Modifier, findServer: NsdServiceInfo) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // 進捗具合
    val progressPair = remember { mutableStateOf(0 to 0) }

    val callback = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents(), onResult = { uriList ->
        // 一時的にコピーする。OkHttpがUri対応してないのでFile使えるようにしないといけない
        progressPair.value = uriList.size to 0
        scope.launch(Dispatchers.IO) {
            uriList.forEachIndexed { index, uri ->
                // 送信
                PhoTransferClientTool.uploadPhoto(
                    context,
                    uri,
                    findServer.host.hostAddress!!,
                    findServer.port
                )
                progressPair.value = uriList.size to index + 1
            }

            // 完了
            if (uriList.isNotEmpty()) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.send_complete), Toast.LENGTH_SHORT).show()
                }
            }
        }
    })

    if (progressPair.value.first == progressPair.value.second) {
        HomeScreenItem(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.manual_send),
            description = stringResource(id = R.string.manual_send_description),
            icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
            onClick = { callback.launch("image/*") }
        )
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