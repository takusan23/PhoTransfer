package io.github.takusan23.photransfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.takusan23.photransfer.ui.screen.MainScreen

/**
 * エントリーポイント
 *
 * UIはComposeで出来ています。
 * */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }

    }

}
