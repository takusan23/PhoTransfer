package io.github.takusan23.photransfer.tool

import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.MimeTypeMap
import io.github.takusan23.client.PhoTransferClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    suspend fun uploadPhoto(context: Context, uri: Uri, ipAddress: String, port: Int): Boolean = withContext(Dispatchers.IO) {
        val fileName = MediaStoreTool.getFileNameFromUri(context, uri)
        val tempPhoto = File(context.externalCacheDir, fileName).apply { createNewFile() }
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(tempPhoto.extension)
        println(mimeType)
        // コピー
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            tempPhoto.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        // 送信
        val isSuccessful = PhoTransferClient.sendData(
            ipAddress = ipAddress,
            deviceName = Build.MODEL,
            port = port,
            file = tempPhoto,
            mimeType = mimeType,
        )
        // 完了
        if (isSuccessful) {
            tempPhoto.delete()
        }
        return@withContext isSuccessful
    }

}