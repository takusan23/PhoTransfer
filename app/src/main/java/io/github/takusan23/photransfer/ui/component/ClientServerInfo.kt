package io.github.takusan23.photransfer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.data.ServerInfoData

/**
 * サーバー/転送先 情報を表示する
 *
 * @param serverInfoData サーバー情報
 * */
@Composable
fun ClientServerInfo(serverInfoData: ServerInfoData) {
    HomeScreenItem(
        text = stringResource(id = R.string.client_server_info),
        description = if (serverInfoData.isLatestServerData) """
            ${stringResource(id = R.string.device_name)} : ${serverInfoData.deviceName}
            ${stringResource(id = R.string.ip_address)} : ${serverInfoData.hostAddress}
            ${stringResource(id = R.string.port)} : ${serverInfoData.portNumber}
            ${stringResource(id = R.string.latest_connection_server_info)}
        """.trimIndent() else """
            ${stringResource(id = R.string.device_name)} : ${serverInfoData.deviceName}
            ${stringResource(id = R.string.ip_address)} : ${serverInfoData.hostAddress}
            ${stringResource(id = R.string.port)} : ${serverInfoData.portNumber}
        """.trimIndent(),
        icon = painterResource(id = R.drawable.ic_outline_perm_device_information_24)
    )
}