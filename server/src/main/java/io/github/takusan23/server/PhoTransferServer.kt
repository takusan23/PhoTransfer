package io.github.takusan23.server

import io.ktor.application.*
import io.ktor.http.*
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
     *     - リクエストヘッダーに User-Agent か Folder-Name で端末名を入れてください。
     *
     * @param port ポート
     * @param saveFolderPath 保存先。
     * @return 端末の名前と保存したファイルのパスをFlowで流します。
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun startServer(port: Int = 4649, saveFolderPath: String) = channelFlow {
        // resources内のindex.htmlを取得。ブラウザ用投稿画面です
        val htmlFile = this@PhoTransferServer::class.java.classLoader.getResource("index.html")!!.readText()
        val server = embeddedServer(Netty, port = port) {
            routing {
                get("/") {
                    call.respondText("""
                        開発者かな？ようこそ。
                        
                        写真アップロードAPIのURLは /upload になります。
                        POSTリクエストで、写真データはMultipart/form-dataで送ってください。
                        nameの値は使わないので適当に埋め、写真ファイルを入れてください。
                        
                        成功するとステータスコード200とOKって文字だけのレスポンスボデーを返します。
                        
                        PCのユーザーはブラウザから投稿できるページを用意してあります。
                        /browser
                        へアクセスし、投稿したい画像をクリップボードから貼り付けて転送を押せばいいです。
                        """.trimIndent())
                }
                get("/browser") {
                    call.respondText(htmlFile, ContentType.parse("text/html"))
                }
                post("/upload") {
                    // コンテキスト切り替えないと怒られる
                    withContext(Dispatchers.IO) {
                        // デバイス名取得。ブラウザ投稿だとUser-Agentを書き換えられないので、別のヘッダーを使う
                        val deviceName = call.request.headers["Folder-Name"] ?: call.request.headers["User-Agent"] ?: "不明なデバイス"
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