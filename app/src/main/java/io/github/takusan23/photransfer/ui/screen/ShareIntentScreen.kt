package io.github.takusan23.photransfer.ui.screen

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.tool.MediaStoreTool
import io.github.takusan23.photransfer.ui.component.*
import io.github.takusan23.photransfer.ui.theme.PhoTransferTheme

/**
 * 共有シートから画像を転送したときに呼ばれる画面
 *
 * @param uriList 共有する画像のUriの配列
 * @param onClose 終了時に呼びますので、Activityを終了させてください
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareIntentScreen(
    uriList: List<Uri>,
    onClose: () -> Unit,
) {
    val context = LocalContext.current
    // 接続先情報
    val networkServiceDiscovery = remember { NetworkServiceDiscovery(context).findServerOrGetLatestServer() }
    val serverInfoData = networkServiceDiscovery.collectAsState(initial = null)
    val carouselImageDataList = remember { mutableStateOf<List<CarouselImageData>>(emptyList()) }

    LaunchedEffect(key1 = Unit) {
        carouselImageDataList.value = uriList.map { uri ->
            val mimeType = MediaStoreTool.getMimeType(context, uri)
            val isPhoto = mimeType?.startsWith("image/") == true
            CarouselImageData(uri, if (isPhoto) CarouselImageType.Photo else CarouselImageType.Video)
        }
    }

    PhoTransferTheme(isDynamicColor = true) {

        Scaffold(
            modifier = Modifier.padding(bottom = 10.dp),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onClose) {
                            Icon(painter = painterResource(id = R.drawable.ic_outline_close_24), contentDescription = null)
                        }
                    },
                    title = { Text(text = stringResource(id = R.string.transfer)) },
                    colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            content = {
                Column(
                    modifier = Modifier.padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 画像をカルーセルみたいに表示
                    CarouselImage(carouselImageDataList = carouselImageDataList.value)
                    // PhoTransferサーバーを検出できれば転送押せるように
                    if (serverInfoData.value != null) {
                        // サーバー/転送先 情報
                        ClientServerInfo(serverInfoData = serverInfoData.value!!)
                        // てんそーボタン
                        ShareIntentTransferButton(
                            serverInfoData = serverInfoData.value!!,
                            uriList = uriList,
                            onFinish = onClose
                        )
                    } else {
                        ServerNotFoundInfo()
                    }
                }
            },
        )
    }

}