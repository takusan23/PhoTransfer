package io.github.takusan23.photransfer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.ui.component.LabelSwitch
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = context.dataStore.data.collectAsState(initial = null)
    val isRunning = dataStore.value?.get(SettingKeyObject.IS_RUNNING) ?: true

    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text(text = stringResource(id = R.string.client_home_title)) })
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                // 有効、無効スイッチ
                LabelSwitch(
                    text = if (isRunning) stringResource(id = R.string.running_client) else stringResource(id = R.string.disable_title),
                    isEnable = isRunning,
                    onValueChange = { value -> scope.launch { context.dataStore.edit { it[SettingKeyObject.IS_RUNNING] = value } } }
                )
            }
        }
    )

}