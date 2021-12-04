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
import java.io.InputStream
import java.io.OutputStream

/**
 * MediaStoreとかいうあんま使いやすくないAPIを簡単に使えるように
 * */
object MediaStoreTool {

    /**
     * MediaStoreに写真を突っ込む
     *
     * Android 10以降
     * -> MediaStoreに登録後UriがもらえるのでOutputStreamを開いて画像データを流す。
     *
     * Android 9以前
     * -> JavaのFileクラスでPicturesフォルダ内にフォルダ作成後、写真ファイルもその中に作成。
     * 画像ファイルのOutputStreamを開き画像データを流し、最後にMediaStoreに登録する。
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
        /**
         * Android 10以降と分岐。Android 10以降はMedia.RELATIVE_PATHでフォルダが作成できるが、
         *
         * Android 9以前には無いのでJavaのFileクラスでファイルを作成して、その後にMediaStoreに登録する
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // MediaStoreへファイル追加
            val uri = try {
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            } catch (e: Exception) {
                // IllegalStateException: Failed to build unique file 例外対策。
                // 新しいファイル（31）以上は生成できない模様。ので適当にユニークなファイル名に書き換える
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues.apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${file.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}")
                })
            } ?: return@withContext
            val outputStream = context.contentResolver.openOutputStream(uri)!!
            val inputStream = file.inputStream()
            // 書き込む
            copy(inputStream, outputStream)
        } else {
            // Android 9
            val phoTransferFolder = File(Environment.DIRECTORY_PICTURES, "PhoTransfer_$deviceName").apply { mkdir() }
            val photoFile = File(phoTransferFolder, file.name).apply { createNewFile() }
            // 書き込む
            val inputStream = file.inputStream()
            val outputStream = photoFile.outputStream()
            copy(inputStream, outputStream)
            // MediaStoreへ登録
            contentValues.put(MediaStore.Images.Media.DATA, photoFile.path)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        }
        // 消すなら消す
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
        copy(inputStream, outputStream)
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

    /**
     * InputStreamとOutputStreamを使った古典的なファイルコピー
     *
     * @param inputStream コピー元
     * @param outputStream コピー先
     * */
    private fun copy(inputStream: InputStream, outputStream: OutputStream) {
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
    }

}