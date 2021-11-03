package io.github.takusan23.photransfer.ui.component

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import io.github.takusan23.photransfer.R

/** URLが置いてある */
object KonoAppComponent {

    /** GitHubのURL */
    const val GITHUB_URL = "https://github.com/takusan23/PhoTransfer"

    /** Twitterリンク */
    const val TWITTER_URL = "https://twitter.com/takusan__23"

}

/**
 * このアプリ画面へ遷移するボタン
 * */
@Composable
fun HomeScreenKonoAppButton(onClick: () -> Unit) {
    HomeScreenItem(
        text = stringResource(id = R.string.kono_app),
        icon = painterResource(id = R.drawable.photransfer_icon),
        onClick = onClick
    )
}

/** このアプリについて、アイコン部分 */
@Composable
fun KonoAppHeader() {
    val context = LocalContext.current
    // アイコン
    val bitmap = remember { ContextCompat.getDrawable(context, R.mipmap.ic_launcher) }!!.toBitmap().asImageBitmap()
    // バージョン
    val version = remember { context.packageManager.getPackageInfo(context.packageName, 0).versionName }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(100.dp)
                .padding(top = 10.dp),
            bitmap = bitmap,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = stringResource(id = R.string.app_name),
            fontSize = 30.sp
        )
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = version,
            fontSize = 20.sp
        )
    }
}

/** このアプリについて、リンク集 */
@Composable
fun KonoAppLinkButton() {
    val context = LocalContext.current

    Column {
        HomeScreenItem(
            text = stringResource(id = R.string.kono_app_source_code),
            description = stringResource(id = R.string.kono_app_source_code_description),
            icon = painterResource(id = R.drawable.ic_outline_developer_mode_24),
            onClick = { launchBrowser(context, KonoAppComponent.GITHUB_URL) }
        )
        HomeScreenItem(
            text = stringResource(id = R.string.kono_app_twitter),
            description = stringResource(id = R.string.kono_app_twitter_description),
            icon = painterResource(id = R.drawable.ic_outline_account_box_24),
            onClick = { launchBrowser(context, KonoAppComponent.TWITTER_URL) }
        )
    }
}

/**
 * ブラウザ起動
 *
 * @param context Context
 * @param url URL
 * */
private fun launchBrowser(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
}