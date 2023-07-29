package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.ui.screen.NavigationLinkList

/** セットアップ時のタイトル画面に表示するやつ */
@Composable
fun SetupTitle() {
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.photransfer_icon),
                modifier = Modifier
                    .padding(10.dp)
                    .size(50.dp),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp),
            )
            Text(
                text = stringResource(id = R.string.about),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(5.dp),
            )
        }
    }
}

/**
 * クライアントかサーバーか選ぶ画面
 *
 * @param onNavigate 遷移してほしいときに呼ばれる
 * */
@Composable
fun SetupServerOrClientSelect(onNavigate: (String) -> Unit) {
    Surface {
        Column(modifier = Modifier.padding(10.dp)) {
            SetupButtons(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = R.string.launch_server),
                icon = painterResource(id = R.drawable.ic_outline_file_download_24),
                description = stringResource(id = R.string.launch_server_description),
                onClick = { onNavigate(NavigationLinkList.ServerSetupScreen) }
            )
            SetupButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                text = stringResource(id = R.string.launch_client),
                icon = painterResource(id = R.drawable.ic_outline_file_upload_24),
                onClick = { onNavigate(NavigationLinkList.ClientSetupScreen) }
            )
        }
    }
}

/**
 * クライアントかサーバーか選ぶ画面で使うボタン
 *
 * @param icon アイコン
 * @param text テキスト
 * @param description 説明
 * @param onClick 押したとき
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SetupButtons(
    modifier: Modifier = Modifier,
    text: String,
    description: String? = null,
    icon: Painter,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = Color.Transparent,
        shape = RoundedCornerShape(size = 10.dp),
        border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp, end = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painter = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(5.dp)
                )
                Text(
                    text = text,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 5.dp),
                    textAlign = TextAlign.Center,
                )
                if (description != null) {
                    Text(
                        modifier = Modifier.padding(bottom = 5.dp),
                        text = description,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}