package io.github.takusan23.photransfer.ui.component

import android.net.nsd.NsdServiceInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R

/**
 * サーバー/転送先 情報を表示する
 *
 * @param nsdServiceInfo サーバー情報
 * */
@Composable
fun ClientServerInfo(nsdServiceInfo: NsdServiceInfo) {
    HomeScreenItem(
        text = stringResource(id = R.string.client_server_info),
        description = """
            ${stringResource(id = R.string.device_name)} : ${nsdServiceInfo.serviceName}
            ${stringResource(id = R.string.ip_address)} : ${nsdServiceInfo.host.hostAddress}
            ${stringResource(id = R.string.port)} : ${nsdServiceInfo.port}
        """.trimIndent(),
        icon = painterResource(id = R.drawable.ic_outline_perm_device_information_24)
    )
}