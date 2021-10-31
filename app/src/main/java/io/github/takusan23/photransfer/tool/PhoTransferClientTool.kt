package io.github.takusan23.photransfer.tool

import android.content.Context
import android.net.Uri
import android.os.Build
import io.github.takusan23.client.PhoTransferClient
import java.io.File

object PhoTransferClientTool {

    /**
     * 転送する
     *
     * @param context Context
     * @param ipAddress IPアドレス
     * @param port ポート番号
     * @param uri 転送したいUri
     * @return 成功したらtrue
     * */
    suspend fun uploadPhoto(context: Context, uri: Uri, ipAddress: String, port: Int): Boolean {
        val fileName = MediaStoreTool.getFileNameFromUri(context, uri)
        val tempPhoto = File(context.externalCacheDir, fileName).apply { createNewFile() }
        // コピー
        MediaStoreTool.copyFile(context, tempPhoto, uri)
        // 送信
        val isSuccessful = PhoTransferClient.sendData(
            ipAddress = ipAddress,
            deviceName = Build.DEVICE,
            port = port,
            file = tempPhoto
        )
        // 完了
        if (isSuccessful) {
            tempPhoto.delete()
        }
        return isSuccessful
    }

}