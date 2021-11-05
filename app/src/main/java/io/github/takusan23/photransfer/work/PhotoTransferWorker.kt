package io.github.takusan23.photransfer.work

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.tool.NetworkCheckTool
import io.github.takusan23.photransfer.tool.PhoTransferClientTool
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.*

/**
 * WorkManagerにやらせることを書く。今回なら写真のアップロード
 * */
class PhotoTransferWorker(val context: Context, workerParams: WorkerParameters) : CoroutineWorker(context, workerParams) {

    /** 通知ID */
    private val NOTIFICATION_ID = 1031

    /** タイムアウト。1分 */
    private val TIMEOUT_MS = 60 * 1000L

    /**
     * お仕事内容。
     * */
    override suspend fun doWork(): Result {
        // Wi-Fiじゃないなら失敗にする
        if (!NetworkCheckTool.isConnectionWiFi(context)) {
            return Result.failure()
        }
        // PhoTransferサーバー探す。10秒以内に見つけることが出来ない場合は終了
        val findServer = withTimeoutOrNull(TIMEOUT_MS) {
            NetworkServiceDiscovery(context).findDevice().first()
        } ?: return Result.failure()
        val ipAddress = findServer.host.hostAddress!!
        val port = findServer.port

        // MediaStoreから写真取得
        val setting = context.dataStore.data.first()
        val latestUploadDate = setting[SettingKeyObject.CLIENT_LATEST_TRANSFER_DATE]!!
        // 転送する写真を選ぶ。最後に転送した時間より新しいものを取得
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.MediaColumns._ID),
            "${MediaStore.MediaColumns.DATE_ADDED} >= ?",
            arrayOf((latestUploadDate / 1000).toString()),
            null
        ) ?: return Result.failure()

        // Uriの配列作成して転送する
        cursor.moveToFirst()
        val taskList = List(cursor.count) {
            val id = cursor.getLong(0)
            // IDからUriをもらう
            val uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            cursor.moveToNext()
            uri
        }.map { uri ->
            // 転送開始
            PhoTransferClientTool.uploadPhoto(context, uri, ipAddress, port)
        }
        cursor.close()

        // 今の日付を入れて、次どこから転送すればいいかわかるようにする
        context.dataStore.edit { it[SettingKeyObject.CLIENT_LATEST_TRANSFER_DATE] = System.currentTimeMillis() }
        // 通知で教える
        showNotification(taskList.count { it })
        return Result.success()
    }

    /**
     * 通知を出す
     * @param count 転送した数
     * */
    private fun showNotification(count: Int) {
        val notificationManager = NotificationManagerCompat.from(context)
        val channelId = "io.github.takusan23.photransfer.transfer_notification"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(context.getString(R.string.transfer_notification))
        }.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(channel)
        }
        val date = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis())
        val notification = NotificationCompat.Builder(context, channelId).apply {
            setContentTitle(context.getString(R.string.transfer_notification))
            setContentText("$date ${context.getString(R.string.transfer_notification_description)} : $count")
            setSmallIcon(R.drawable.photransfer_icon)
        }.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

}