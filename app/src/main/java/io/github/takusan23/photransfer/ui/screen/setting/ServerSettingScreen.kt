package io.github.takusan23.photransfer.ui.screen.setting

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.ui.component.PermissionCard
import io.github.takusan23.photransfer.ui.screen.NavigationLinkList
import kotlinx.coroutines.launch

/**
 * サーバー設定画面。ポート番号とか？
 *
 * @param onBack 戻ってほしいときに呼ばれる
 * @param onNavigate 画面遷移してほしいときに呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerSettingScreen(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val portNumber = remember { mutableStateOf(SettingKeyObject.DEFAULT_PORT_NUMBER.toString()) }
    val isPermissionGranted = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(text = stringResource(id = R.string.server_setting)) },
                navigationIcon = { IconButton(onClick = onBack, content = { Icon(painter = painterResource(id = R.drawable.ic_outline_arrow_back_24), contentDescription = null) }) }
            )
        },
        content = {
            Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp)) {
                Column {
                    // ポート番号
                    Text(
                        text = stringResource(id = R.string.setting_port_number_description),
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth(),
                        value = portNumber.value,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(text = stringResource(id = R.string.setting_port_number_title)) },
                        onValueChange = { value -> portNumber.value = value }
                    )

                    // Android 9以前は権限もらう
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                        PermissionCard(modifier = Modifier.padding(top = 10.dp)) {
                            isPermissionGranted.value = true
                        }
                    }

                }
            }
        },
        floatingActionButton = {
            val isShow = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                isPermissionGranted.value
            } else true
            if (isShow) {
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(id = R.string.start_server)) },
                    icon = { Icon(painter = painterResource(id = R.drawable.ic_outline_directions_run_24), contentDescription = null) },
                    onClick = {
                        // 設定書き込み
                        scope.launch {
                            context.dataStore.edit {
                                it[SettingKeyObject.IS_RUNNING] = true
                                it[SettingKeyObject.MODE] = SettingKeyObject.MODE_SERVER
                                it[SettingKeyObject.PORT_NUMBER] = portNumber.value.toInt()
                            }
                            onNavigate(NavigationLinkList.HomeScreen)
                        }
                    }
                )
            }
        }
    )
}