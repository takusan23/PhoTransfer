package io.github.takusan23.photransfer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.takusan23.photransfer.ui.screen.MainScreen
import io.github.takusan23.photransfer.ui.screen.ShareIntentScreen

/**
 * エントリーポイント
 *
 * UIはComposeで出来ています。
 * */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 共有から来た場合
        val uriList = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM) ?: listOfNotNull(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))

        setContent {
            if (uriList.isNotEmpty()) {
                // 共有からのお客さん
                ShareIntentScreen(
                    uriList = uriList,
                    onClose = { finishAndRemoveTask() }
                )
            } else {
                // メイン画面
                MainScreen()
            }
        }

    }

}
