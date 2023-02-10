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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import io.github.takusan23.photransfer.R

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
    carouselImageDataList: List<CarouselImageData>,
) {
    LazyRow(
        modifier = modifier,
        state = lazyListState,
        contentPadding = PaddingValues(horizontal = spaceDp, vertical = spaceDp),
        content = {
            items(carouselImageDataList) { data ->
                Surface(
                    modifier = Modifier.padding(end = 10.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    content = {
                        if (data.type == CarouselImageType.Photo) {
                            Image(
                                modifier = Modifier
                                    .size(200.dp),
                                painter = rememberImagePainter(data = data.uri),
                                contentDescription = null
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .size(200.dp),
                                painter = painterResource(id = R.drawable.outline_video_file_24),
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        }
    )
}

data class CarouselImageData(
    val uri: Uri,
    val type: CarouselImageType
)

enum class CarouselImageType {
    Photo,
    Video
}