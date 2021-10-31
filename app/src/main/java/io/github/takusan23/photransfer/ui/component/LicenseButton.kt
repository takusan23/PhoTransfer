package io.github.takusan23.photransfer.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.ui.screen.NavigationLinkList

/**
 * ライセンス画面
 *
 * @param onNavigate 画面遷移してほしいときに呼ばれます。
 * */
@Composable
fun LicenseButton(onNavigate: (String) -> Unit) {
    HomeScreenItem(
        text = stringResource(id = R.string.license),
        icon = painterResource(id = R.drawable.ic_outline_info_24),
        onClick = { onNavigate(NavigationLinkList.LicenseScreen) }
    )
}