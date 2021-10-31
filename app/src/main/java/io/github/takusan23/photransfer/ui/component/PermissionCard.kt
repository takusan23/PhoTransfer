package io.github.takusan23.photransfer.ui.component

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.photransfer.R

/**
 * Android 9以前で表示する、ファイル書き込み権限ください画面
 *
 * @param modifier Modifier
 * @param onGranted 権限が付与されたら呼ばれる
 * */
@Composable
fun StorageWritePermissionCard(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit,
) {

    // 権限コールバック
    val permissionCallback = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
        if (isGranted) {
            onGranted()
        }
    })

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(start = 10.dp),
                    painter = painterResource(id = R.drawable.ic_outline_info_24),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.request_storage_write_permission_description),
                    modifier = Modifier.padding(10.dp),
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 10.dp, end = 10.dp),
                onClick = { permissionCallback.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) },
                content = { Text(text = stringResource(id = R.string.permission_request_button)) }
            )
        }
    }
}

/**
 * ストレージ読み込み権限をもらう。
 *
 * MediaStoreで他アプリの追加した画像を取得するため
 *
 * @param modifier Modifier
 * @param onGranted 権限が付与されたら呼ばれる
 * */
@Composable
fun StorageReadPermissionCard(
    modifier: Modifier = Modifier,
    onGranted: () -> Unit,
) {
    // 権限コールバック
    val permissionCallback = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission(), onResult = { isGranted ->
        if (isGranted) {
            onGranted()
        }
    })

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.padding(start = 10.dp),
                    painter = painterResource(id = R.drawable.ic_outline_info_24),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.request_storage_read_permission_description),
                    modifier = Modifier.padding(10.dp),
                )
            }
            Button(
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(bottom = 10.dp, end = 10.dp),
                onClick = { permissionCallback.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE) },
                content = { Text(text = stringResource(id = R.string.permission_request_button)) }
            )
        }
    }
}