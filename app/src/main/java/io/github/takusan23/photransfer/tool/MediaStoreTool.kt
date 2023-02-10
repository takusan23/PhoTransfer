package io.github.takusan23.photransfer.tool

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.contentValuesOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * MediaStoreとかいうあんま使いやすくないAPIを簡単に使えるように
 * */
object MediaStoreTool {

    /** 動画ファイル拡張子 */
    private val VIDEO_FILE_EXTENSION = listOf("mp4", "webm", "ts")

    /**
     * MediaStoreに写真/動画を突っ込む
     *
     * Android 10以降
     * -> MediaStoreに登録後UriがもらえるのでOutputStreamを開いて画像データを流す。
     *
     * Android 9以前
     * -> JavaのFileクラスでPicturesフォルダ内にフォルダ作成後、写真ファイルもその中に作成。
     * 画像ファイルのOutputStreamを開き画像データを流し、最後にMediaStoreに登録する。
     *
     * @param context Context
     * @param deviceName フォルダ名の PhoTransfer_<フォルダ名> ←ここの名前
     * @param file 書き込むファイル
     * @param mimeType MIME Type
     * @param isFileDelete 追加後削除するか
     * */
    suspend fun insertPhotoOrVideo(
        context: Context,
        deviceName: String,
        originalFileName: String,
        mimeType: String,
        file: File,
        isFileDelete: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val isVideo = VIDEO_FILE_EXTENSION.contains(file.extension)
        val contentValues = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValuesOf(
                MediaStore.Images.Media.DISPLAY_NAME to file.name,
                MediaStore.Images.Media.MIME_TYPE to mimeType,
                // 写真/動画 フォルダに 「PhoTransfer_<デバイス名>」 フォルダを作成する
                MediaStore.Images.Media.RELATIVE_PATH to "${if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES}/PhoTransfer_$deviceName"
            )
        } else {
            contentValuesOf(
                MediaStore.Images.Media.DISPLAY_NAME to file.name,
                MediaStore.Images.Media.MIME_TYPE to mimeType,
            )
        }

        /**
         * Android 10以降と分岐。Android 10以降はMedia.RELATIVE_PATHでフォルダが作成できるが、
         *
         * Android 9以前には無いのでJavaのFileクラスでファイルを作成して、その後にMediaStoreに登録する
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val insertTo = if (isVideo) MediaStore.Video.Media.EXTERNAL_CONTENT_URI else MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            // MediaStoreへファイル追加
            val uri = try {
                context.contentResolver.insert(insertTo, contentValues)
            } catch (e: Exception) {
                // IllegalStateException: Failed to build unique file 例外対策。
                // 新しいファイル（31）以上は生成できない模様。ので適当にユニークなファイル名に書き換える
                context.contentResolver.insert(insertTo, contentValues.apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "${file.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}")
                })
            } ?: return@withContext
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } else {
            // Android 9
            val folder = if (isVideo) Environment.DIRECTORY_MOVIES else Environment.DIRECTORY_PICTURES
            val phoTransferFolder = File(folder, "PhoTransfer_$deviceName").apply { mkdir() }
            val photoFile = File(phoTransferFolder, originalFileName).apply { createNewFile() }
            // 書き込む
            photoFile.outputStream().use { outputStream ->
                file.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
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
     * 写真のUriからファイル名を取得する
     *
     * @param context Context
     * @param uri Uri
     * @return ファイル名
     * */
    suspend fun getFileNameFromUri(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val fileName = context.contentResolver.query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }
        return@withContext fileName ?: "不明"
    }

    /**
     * MIME-TYPE を返す
     *
     * @param context [Context]
     * @param uri [Uri]
     * @return MIME-TYPE
     */
    suspend fun getMimeType(context: Context, uri: Uri) = withContext(Dispatchers.IO) {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.MIME_TYPE),
            null,
            null,
            null
        )?.use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }
        return@withContext mimeType
    }

}