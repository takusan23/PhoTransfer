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
     * Androidで使う場合はHTTP通信を明示的に有効にする必要があります
     *
     * Multipart/form-dataで送る
     *
     * @param file 送信するファイル
     * @param ipAddress サーバーのIPアドレス
     * @param deviceName クライアントのデバイス名
     * @param port ポート番号
     * @return 成功したらtrue
     * */
    suspend fun sendData(ipAddress: String = "localhost", deviceName: String, port: Int = 4649, file: File) = withContext(Dispatchers.Default) {
        // 送るデータ
        val formData = MultipartBody.Builder().apply {
            addFormDataPart("photo", file.name, file.asRequestBody())
        }.build()
        val request = Request.Builder().apply {
            url("http://${ipAddress}:${port}/upload")
            addHeader("User-Agent", deviceName)
            post(formData)
        }.build()
        val response = okHttpClient.newCall(request).execute()
        return@withContext response.isSuccessful
    }

    /**
     * サーバーにアクセスできるかの疎通確認を行う
     *
     * @param ipAddress サーバーのIPアドレス
     * @param port ポート番号
     * @return 成功時はtrue
     * */
    suspend fun checkIsServerLive(ipAddress: String = "localhost", port: Int = 4649) = withContext(Dispatchers.Default) {
        // 適当に送って200ならok
        val request = Request.Builder().apply {
            url("http://${ipAddress}:${port}")
            get()
        }.build()
        return@withContext try {
            val response = okHttpClient.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

}