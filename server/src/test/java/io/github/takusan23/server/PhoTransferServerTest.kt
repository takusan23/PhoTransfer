package io.github.takusan23.server

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PhoTransferServerTest {

    @Test
    fun testStartServer() {
        val server = PhoTransferServer()
        // Windows以外は知らん。転送先フォルダ
        val windowsUserDownloadFolderPath = """${System.getProperty("user.home")}\Downloads"""
        runBlocking {
            // サーバー起動。APIのテストが出来ます。
            server.startServer(saveFolderPath = windowsUserDownloadFolderPath).collect {
                println(it)
            }
        }
    }

}