package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * 写真転間隔を表示する
 *
 * @param dataStore DataStore
 * @param onValueChange 値が変更されたら呼ばれる
 * */
@Composable
fun ClientTransferInterval(
    dataStore: Preferences?,
    onValueChange: () -> Unit = { },
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isShow = remember { mutableStateOf(false) }
    val textValue = remember { mutableStateOf("") }

    /** DataStoreへ保存する */
    fun save() {
        isShow.value = false
        val minute = textValue.value.toLongOrNull() ?: 60L
        if (minute >= 15) {
            // 保存して再起動
            scope.launch { context.dataStore.edit { it[SettingKeyObject.CLIENT_TRANSFER_INTERVAL_MINUTE] = minute } }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        context.dataStore.data.map { it[SettingKeyObject.CLIENT_TRANSFER_INTERVAL_MINUTE] }.collect {
            textValue.value = (it ?: SettingKeyObject.DEFAULT_CLIENT_TRANSFER_INTERVAL_MINUTE).toString()
        }
    })

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HomeScreenItem(
                modifier = Modifier.weight(1f),
                text = stringResource(id = R.string.setting_interval_minute),
                description = """
                    ${stringResource(id = R.string.setting_interval_minute_description)}
                    ${textValue.value} ${stringResource(id = R.string.minute)}
                """.trimIndent(),
                icon = painterResource(id = R.drawable.ic_outline_schedule_send_24)
            )
            IconButton(
                modifier = Modifier.padding(end = 10.dp),
                onClick = { isShow.value = !isShow.value },
                content = { Icon(painter = painterResource(id = R.drawable.ic_outline_create_24), contentDescription = null) }
            )
        }
        if (isShow.value) {
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp, bottom = 10.dp, start = 20.dp, end = 10.dp),
                        value = textValue.value,
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(fontSize = 16.sp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { save() }),
                        onValueChange = { textValue.value = it }
                    )
                    IconButton(
                        modifier = Modifier.padding(end = 10.dp),
                        onClick = { save() },
                        content = { Icon(painter = painterResource(id = R.drawable.ic_outline_done_24), contentDescription = null) }
                    )
                }
            }
        }
    }
}