package io.github.takusan23.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * サーバーに写真をアップロードする
 * */
object PhoTransferClient {

    /** OkHttpクライアントは使い回すとパフォーマンスがいいらしい */
    private val okHttpClient = OkHttpClient()

    /**
     * データをサーバーに送信する。
     *
     * Multipart/form-dataで送る
     *
     * @param file 送信するファイル
     * @param port ポート番号
     * */
    suspend fun sendData(ipAddress: String = "localhost", port: Int = 4649, file: File) = withContext(Dispatchers.Default) {
        // 送るデータ
        val formData = MultipartBody.Builder().apply {
            addFormDataPart("photo", file.name, file.asRequestBody())
        }.build()
        val request = Request.Builder().apply {
            url("${ipAddress}:${port}/upload")
            post(formData)
        }.build()
        okHttpClient.newCall(request).execute()
    }

}