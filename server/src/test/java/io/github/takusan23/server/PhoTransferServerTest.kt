package io.github.takusan23.server

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PhoTransferServerTest {

    @Test
    fun testStartServer() {
        val server = PhoTransferServer()
        // Windows以外は知らん。
        val windowsUserDownloadFolderPath = """${System.getProperty("user.home")}\Downloads"""
        runBlocking {
            server.startServer(saveFolderPath = windowsUserDownloadFolderPath).collect {
                println(it)
            }
        }
    }

}