package io.github.takusan23.photransfer.ui.component

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.datastore.preferences.core.Preferences
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.tool.IPAddressTool

/**
 * ブラウザ版クライアントのリンクを表示する
 *
 * @param dataStore DataStore
 * */
@Composable
fun BrowserLinkInfo(dataStore: Preferences) {
    val context = LocalContext.current
    val ipAddress = remember { IPAddressTool.collectIpAddress(context) }.collectAsState(initial = null)
    val port = dataStore[SettingKeyObject.PORT_NUMBER]
    val url = "http://${ipAddress.value}:$port/browser"

    HomeScreenItem(
        text = stringResource(id = R.string.browser_client_title),
        description = url,
        icon = painterResource(id = R.drawable.ic_outline_open_in_browser_24),
        onClick = { launchURL(context, url) }
    )
}

/**
 * ブラウザを起動する
 *
 * @param context Context
 * @param url URL
 * */
private fun launchURL(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    context.startActivity(intent)
}