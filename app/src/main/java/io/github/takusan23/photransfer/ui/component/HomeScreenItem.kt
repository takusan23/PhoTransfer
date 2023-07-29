package io.github.takusan23.photransfer.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
 * @param shape 丸くしたいなら
 * */
@OptIn(ExperimentalMaterial3Api::class)
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
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        content = {
            CommonHomeScreenItem(
                icon = icon,
                text = text,
                description = description
            )
        }
    )
}

/**
 * スイッチ付き項目
 *
 * @param modifier Modifier
 * @param icon アイコン
 * @param text テキスト
 * @param description 説明
 * @param containerColor 背景色
 * @param contentColor コンテンツの色
 * @param isEnable スイッチ有効ならtrue
 * @param onValueChange スイッチの状態切り替わったら呼ばれる
 * @param shape 丸くしたいなら
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenSwitchItem(
    modifier: Modifier = Modifier,
    text: String,
    isEnable: Boolean,
    description: String? = null,
    icon: Painter,
    containerColor: Color = Color.Transparent,
    shape: Shape = RectangleShape,
    contentColor: Color = contentColorFor(backgroundColor = containerColor),
    onValueChange: (Boolean) -> Unit = {},
) {
    Surface(
        modifier = modifier,
        onClick = { onValueChange(!isEnable) },
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommonHomeScreenItem(
                modifier = Modifier.weight(1f),
                icon = icon,
                text = text,
                description = description
            )
            Switch(
                modifier = Modifier.padding(10.dp),
                checked = isEnable,
                onCheckedChange = onValueChange
            )
        }
    }
}

/**
 * 進捗付き項目
 *
 * @param modifier Modifier
 * @param icon アイコン
 * @param text テキスト
 * @param max 最大値
 * @param current 現在の値
 * @param description 説明
 * @param containerColor 背景色
 * @param contentColor コンテンツの色
 * @param onClick 押したとき
 * @param shape 丸くしたいなら
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenProgressItem(
    modifier: Modifier = Modifier,
    text: String,
    max: Int = 10,
    current: Int = 5,
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
        color = containerColor,
        shape = shape,
        contentColor = contentColor,
        content = {
            Column {
                CommonHomeScreenItem(
                    icon = icon,
                    text = text,
                    description = description
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    progress = current.toFloat() / max.toFloat()
                )
            }
        }
    )

}

/**
 * [HomeScreenItem]などの共通部分
 *
 * @param modifier Modifier
 * @param contentColor ContentColor
 * @param icon アイコン
 * @param text テキスト
 * @param description 説明
 * */
@Composable
private fun CommonHomeScreenItem(
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current,
    icon: Painter,
    text: String,
    description: String?,
) {
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