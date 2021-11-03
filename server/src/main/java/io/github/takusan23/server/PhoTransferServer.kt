package io.github.takusan23.server

import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import java.io.File

class PhoTransferServer {

    /**
     * サーバー起動。写真アップロードを待ち受けるサーバー
     *
     * ちなみに別にMultipart/form-dataで送信できれば写真以外も行けるはず
     *
     * 写真アップロードAPIは以下
     *
     * - /upload
     *     - POST
     *     - Content-Type : Multipart/form-data
     *     - 名前は適当に埋めてください。ファイルの名前を使うので
     *
     * @param port ポート
     * @param saveFolderPath 保存先。
     * @return 端末の名前と保存したファイルのパスをFlowで流します。
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startServer(port: Int = 4649, saveFolderPath: String) = channelFlow {
        val server = embeddedServer(Netty, port = port) {
            routing {
                get("/") {
                    call.respondText("""
                        開発者かな？ようこそ。
                        
                        写真アップロードAPIのURLは /upload になります。
                        POSTリクエストで、写真データはMultipart/form-dataで送ってください。
                        nameの値は使わないので適当に埋め、写真ファイルを入れてください。
                        
                        成功するとステータスコード200とOKって文字だけのレスポンスボデーを返します。
                        """.trimIndent())
                }
                post("/upload") {
                    // コンテキスト切り替えないと怒られる
                    withContext(Dispatchers.IO) {
                        // デバイス名取得
                        val deviceName = call.request.headers["User-Agent"] ?: "不明なデバイス"
                        // データ受け取る
                        val multipartData = call.receiveMultipart()
                        multipartData.forEachPart { part ->
                            if (part is PartData.FileItem) {
                                val name = part.originalFileName!!
                                val byteArray = part.provider().readBytes()
                                // ファイル作成
                                val receiveFile = File(saveFolderPath, name).apply {
                                    createNewFile()
                                    writeBytes(byteArray)
                                }
                                // ファイルパスをFlowに流す
                                trySend(deviceName to receiveFile.path)
                            }
                        }
                        call.respondText("OK")
                    }
                }
            }
        }
        // サーバースタート。スレッドブロックはしない(Flowで流すので止められたら困る)
        server.start(wait = false)
       // awaitClose { server.stop(1000, 1000) }
    }

}