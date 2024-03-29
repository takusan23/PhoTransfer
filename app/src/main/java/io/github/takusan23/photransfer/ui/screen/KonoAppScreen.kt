package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.ui.component.KonoAppHeader
import io.github.takusan23.photransfer.ui.component.KonoAppLinkButton

/**
 * このアプリについて画面
 *
 * @param onBack 画面戻ってほしいときに使う
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonoAppScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.kono_app)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) } }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            // アイコンと名前
            KonoAppHeader()
            // リンク
            KonoAppLinkButton()
        }
    }
}