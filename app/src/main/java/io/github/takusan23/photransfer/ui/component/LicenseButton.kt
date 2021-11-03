package io.github.takusan23.photransfer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R

/**
 * ライセンス画面
 *
 * @param onClick 押したとき。
 * */
@Composable
fun LicenseButton(onClick: () -> Unit) {
    HomeScreenItem(
        text = stringResource(id = R.string.license),
        icon = painterResource(id = R.drawable.ic_outline_info_24),
        onClick = onClick
    )
}