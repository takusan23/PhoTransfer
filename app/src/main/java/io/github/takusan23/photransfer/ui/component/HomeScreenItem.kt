package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ホーム画面の項目の共通部分
 *
 * @param modifier Modifier
 * @param icon アイコン
 * @param text テキスト
 * @param description 説明
 * @param containerColor 背景色
 * @param contentColor コンテンツの色
 * @param onClick 押したとき
 * */
@Composable
fun HomeScreenItem(
    modifier: Modifier = Modifier,
    text: String,
    description: String? = null,
    icon: Painter,
    containerColor: Color = Color.Transparent,
    shape: Shape = RectangleShape,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    onClick: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(),
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        content = {
            Row(
                modifier = modifier.padding(top = 5.dp, bottom = 5.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 10.dp),
                    painter = icon,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(color = contentColor)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = text,
                        modifier = Modifier.padding(bottom = 5.dp),
                        fontSize = 18.sp
                    )
                    if (description != null) {
                        Text(text = description)
                    }
                }
            }
        }
    )
}