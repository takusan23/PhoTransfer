package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
@Composable
fun LabelSwitch(
    text: String,
    isEnable: Boolean,
    onValueChange: (Boolean) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        onClick = { onValueChange(!isEnable) },
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
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
            AndroidSnowConeSwitch(
                modifier = Modifier.padding(end = 10.dp),
                isEnable = isEnable,
                onValueChange = onValueChange
            )
        }
    }
}