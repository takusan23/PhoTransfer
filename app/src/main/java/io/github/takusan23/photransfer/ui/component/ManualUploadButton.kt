package io.github.takusan23.photransfer.ui.component

import android.net.nsd.NsdServiceInfo
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
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

    val callback = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents(), onResult = { uriList ->
        // 一時的にコピーする。OkHttpがUri対応してないのでFile使えるようにしないといけない
        uriList.forEach { uri ->
            scope.launch(Dispatchers.IO) {
                // 送信
                val isSuccessful = PhoTransferClientTool.uploadPhoto(
                    context,
                    uri,
                    findServer.host.hostAddress!!,
                    findServer.port
                )
                // 完了
                if (isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.send_complete), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    })

    HomeScreenItem(
        modifier = modifier.fillMaxWidth(),
        text = stringResource(id = R.string.manual_send),
        description = stringResource(id = R.string.manual_send_description),
        icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
        onClick = { callback.launch("image/*") }
    )
}