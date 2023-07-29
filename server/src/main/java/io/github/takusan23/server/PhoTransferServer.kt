package io.github.takusan23.server

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.http.content.resource
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.utils.io.core.copyTo
import io.ktor.utils.io.streams.asOutput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
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
    suspend fun startServer(port: Int = 4649, saveFolderPath: String) = channelFlow {
        val server = embeddedServer(Netty, port = port) {
            routing {
                get("/") {
                    call.respondText(
                        """
                        開発者かな？ようこそ。
                        
                        写真アップロードAPIのURLは /upload になります。
                        POSTリクエストで、写真データはMultipart/form-dataで送ってください。
                        nameの値は使わないので適当に埋め、写真ファイルを入れてください。
                        
                        成功するとステータスコード200とOKって文字だけのレスポンスボデーを返します。
                        
                        PCのユーザーはブラウザから投稿できるページを用意してあります。
                        /browser
                        へアクセスし、投稿したい画像をクリップボードから貼り付けて転送を押せばいいです。
                        """.trimIndent()
                    )
                }

                // resources内のindex.htmlを取得。ブラウザ用投稿画面です
                resource("/browser", "index.html")

                post("/upload") {
                    // コンテキスト切り替えないと怒られる
                    withContext(Dispatchers.IO) {
                        // デバイス名取得。ブラウザ投稿だとUser-Agentを書き換えられないので、別のヘッダーを使う
                        val deviceName = call.request.headers["Folder-Name"] ?: call.request.headers["User-Agent"] ?: "不明なデバイス"
                        // データ受け取る
                        val multipartData = call.receiveMultipart()
                        multipartData.forEachPart { part ->
                            if (part is PartData.FileItem) {
                                // ファイル作成
                                val fileName = part.originalFileName ?: System.currentTimeMillis().toString()
                                // コピーする
                                val receiveFile = File(saveFolderPath, fileName).apply {
                                    createNewFile()
                                }
                                part.provider().use { input ->
                                    // use でリソースを自動開放
                                    receiveFile.outputStream().asOutput().use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                // ファイルパスをFlowに流す
                                trySend(PhoTransferData(
                                    deviceName = deviceName,
                                    originalName = fileName,
                                    filePath = receiveFile.path,
                                    mimeType = part.contentType?.let { "${it.contentType}/${it.contentSubtype}" } ?: "image/png"
                                ))
                            }
                        }
                        call.respondText("OK")
                    }
                }
            }
        }
        // サーバースタート。スレッドブロックはしない(Flowで流すので止められたら困る)
        server.start(wait = false)
        awaitClose { server.stop(1000, 1000) }
    }

}