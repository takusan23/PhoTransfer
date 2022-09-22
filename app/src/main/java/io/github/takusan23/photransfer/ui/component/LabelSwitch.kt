package io.github.takusan23.photransfer.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * テキストとスイッチが付いたやつ
 *
 * サーバー、クライアント 有効、無効スイッチで使ってる
 *
 * @param text テキスト
 * @param isEnable 有効ならtrue
 * @param onValueChange 有効、無効切り替わったら呼ばれる
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabelSwitch(
    text: String,
    isEnable: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    val backgroundColor = animateColorAsState(targetValue = if (isEnable) MaterialTheme.colorScheme.primaryContainer else Color.LightGray)
    Surface(
        color = backgroundColor.value,
        onClick = { onValueChange(!isEnable) },
        shape = RoundedCornerShape(30.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp, end = 10.dp, top = 30.dp, bottom = 30.dp),
                text = text,
                fontSize = 18.sp
            )
            Switch(
                modifier = Modifier.padding(end = 10.dp),
                checked = isEnable,
                onCheckedChange = onValueChange
            )
        }
    }
}