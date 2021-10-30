package io.github.takusan23.photransfer.ui.component

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.client.PhoTransferClient
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.tool.MediaStoreTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 手動でローカルの写真を選んでアップロードする
 *
 * @param modifier Modifier
 * */
@Composable
fun ManualUploadButton(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    // サーバー検索
    val serverFindFlow = remember { NetworkServiceDiscovery(context).findDevice() }
    val findServer = serverFindFlow.collectAsState(initial = null)

    val callback = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents(), onResult = { uriList ->
        // 一時的にコピーする。OkHttpがUri対応してないのでFile使えるようにしないといけない
        uriList.forEach { uri ->
            scope.launch(Dispatchers.IO) {
                val fileName = MediaStoreTool.getFileNameFromUri(context, uri)
                val tempPhoto = File(context.externalCacheDir, fileName).apply { createNewFile() }
                // コピー
                MediaStoreTool.copyFile(context, tempPhoto, uri)
                // 送信
                val isSuccessful = PhoTransferClient.sendData(
                    ipAddress = findServer.value!!.host.hostAddress!!,
                    deviceName = Build.DEVICE,
                    port = findServer.value!!.port,
                    file = tempPhoto
                )
                // 完了
                if (isSuccessful) {
                    tempPhoto.delete()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.send_complete), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    })

    if (findServer.value != null) {
        HomeScreenItem(
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.manual_send),
            description = """
            ${stringResource(id = R.string.send_device_name)} : ${findServer.value?.serviceName}
            ${stringResource(id = R.string.ip_address)} : ${findServer.value?.host?.hostAddress}
        """.trimIndent(),
            icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
            onClick = {
                if (findServer.value != null) callback.launch("image/*")
            }
        )
    } else {
        // 接続を失った
        HomeScreenItem(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.error,
            shape = RoundedCornerShape(20.dp),
            modifier = modifier.fillMaxWidth(),
            text = stringResource(id = R.string.manual_send),
            description = stringResource(id = R.string.not_found_device),
            icon = painterResource(id = R.drawable.ic_outline_error_outline_24),
        )
    }
}