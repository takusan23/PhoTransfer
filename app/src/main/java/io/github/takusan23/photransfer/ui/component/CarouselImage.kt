package io.github.takusan23.photransfer.ui.component

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

/**
 * 横並びの画像。
 *
 * @param uriList 表示する画像のUriを配列で
 * */
@Composable
fun CarouselImage(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    spaceDp: Dp = 10.dp,
    uriList: List<Uri>,
) {
    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = spaceDp, vertical = spaceDp),
        content = {
            items(uriList) { uri ->
                Surface(
                    modifier = Modifier.padding(end = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    content = {
                        Image(
                            modifier = Modifier
                                .size(200.dp),
                            painter = rememberImagePainter(data = uri),
                            contentDescription = null
                        )
                    }
                )
            }
        }
    )
}