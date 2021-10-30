package io.github.takusan23.photransfer.tool

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * MediaStoreとかいうあんま使いやすくないAPIを簡単に使えるように
 * */
object MediaStoreTool {

    /**
     * MediaStoreに写真を突っ込む
     *
     * Android、JavaのFileクラスで直接写真フォルダを選ぶことは禁止されているので、MediaStoreとかいうDBに写真の情報追加して
     *
     * Uriを発行してもらい、UriからOutputStreamを開いて画像データを流す。クソめんどいんだけどこれ
     *
     * @param context Context
     * @param deviceName Pictures/PhoTransfer_<フォルダ名> ←ここの名前
     * @param file 書き込むファイル
     * @param isFileDelete 追加後削除するか
     * */
    suspend fun insertPhoto(context: Context, deviceName: String, file: File, isFileDelete: Boolean = false) = withContext(Dispatchers.IO) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            put(MediaStore.Images.Media.MIME_TYPE, MimeTypeMap.getFileExtensionFromUrl(file.path))
            // 写真フォルダに 「PhoTransfer_<デバイス名>」 フォルダを作成する
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PhoTransfer_$deviceName")
            }
        }
        // ファイル追加
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return@withContext
        // OutputStreamもらう
        val outputStream = context.contentResolver.openOutputStream(uri)!!
        val inputStream = file.inputStream()
        // 書き込む
        val buffer = ByteArray(1024 * 4096)
        while (true) {
            val read = inputStream.read(buffer)
            if (read == -1) {
                // もう取れない
                break
            }
            outputStream.write(buffer, 0, read)
        }
        // 終了処理
        outputStream.close()
        inputStream.close()
        if (isFileDelete) {
            file.delete()
        }
    }

    /**
     * UriのデータをFileクラスで扱えるようにアプリ専用ストレージにコピーする
     *
     * @param context Context
     * @param file コピー先
     * @param uri コピー元
     * @param コピー先ファイルパス
     * */
    suspend fun copyFile(context: Context, file: File, uri: Uri) = withContext(Dispatchers.IO) {
        // OutputStreamもらう
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val outputStream = file.outputStream()
        // 書き込む
        val buffer = ByteArray(1024 * 4096)
        while (true) {
            val read = inputStream.read(buffer)
            if (read == -1) {
                // もう取れない
                break
            }
            outputStream.write(buffer, 0, read)
        }
        // 終了処理
        outputStream.close()
        inputStream.close()
        return@withContext file.path
    }

    /**
     * 写真のUriからファイル名を取得する
     *
     * @param context Context
     * @param uri Uri
     * @return ファイル名
     * */
    suspend fun getFileNameFromUri(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        var fileName = "不明"
        context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.apply {
            moveToFirst()
            fileName = getString(0)
            close()
        }
        return@withContext fileName
    }

}