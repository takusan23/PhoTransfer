package io.github.takusan23.photransfer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import io.github.takusan23.photransfer.ui.screen.MainScreen
import io.github.takusan23.server.PhoTransferServer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }

        lifecycleScope.launch {
            val server = PhoTransferServer()
            server.startServer(4649, getExternalFilesDir(null)!!.path).collect {

            }
        }

    }

}
