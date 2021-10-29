package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import io.github.takusan23.photransfer.ui.component.SetupServerOrClientSelect
import io.github.takusan23.photransfer.ui.component.SetupTitle

/**
 * クライアントモードかサーバーモードか
 *
 * @param onNavigate 遷移してほしいときに呼ばれる。setup/clientとか
 * */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectSetupScreen(
    onNavigate: (String) -> Unit,
) {
    Column {
        SetupTitle()
        Divider()
        SetupServerOrClientSelect(onNavigate = onNavigate)
    }
}