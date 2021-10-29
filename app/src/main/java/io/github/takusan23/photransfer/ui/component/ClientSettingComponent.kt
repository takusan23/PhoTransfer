package io.github.takusan23.photransfer.ui.component

import android.net.nsd.NsdServiceInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.photransfer.R

/** 検索中...画面 */
@Composable
fun DeviceFindText() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(10.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = stringResource(id = R.string.find_device))
        }
    }
}

/**
 * デバイス検索結果
 *
 * @param nsdServiceInfo 情報
 * */
@Composable
fun DeviceFindResult(nsdServiceInfo: NsdServiceInfo) {
    Column {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = "${stringResource(id = R.string.device_name)} : ${nsdServiceInfo.serviceName}",
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 18.sp
                )
                Text(
                    text = "${stringResource(id = R.string.ip_address)} : ${nsdServiceInfo.host}",
                    modifier = Modifier.padding(top = 5.dp),
                )
                Text(
                    text = "${stringResource(id = R.string.port)} : ${nsdServiceInfo.port}",
                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_outline_info_24),
                contentDescription = null,
                modifier = Modifier.padding(10.dp)
            )
            Text(text = stringResource(id = R.string.client_send_message))
        }
    }
}