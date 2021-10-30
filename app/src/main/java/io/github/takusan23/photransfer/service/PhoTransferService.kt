package io.github.takusan23.photransfer.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.NetworkServiceDiscovery
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.tool.MediaStoreTool
import io.github.takusan23.server.PhoTransferServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class PhoTransferService : Service() {

    /** 通知マネージャー */
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    /** 通知のID */
    private val NOTIFICATION_ID = 1030

    /** コルーチンスコープ */
    private val scope = CoroutineScope(Dispatchers.Main)

    /** 一時保存先。
     *
     * ここに保存したあとMediaStoreに登録して、端末のギャラリーアプリから見れるようにする。
     *
     * ギャラリーから見れるようになればGoogleフォトのメディアスキャンに引っかかるはず。
     * */
    private val receivePhotoFolder by lazy { File(externalCacheDir, "receive_photo").apply { mkdir() } }

    /**
     * 通知を消せるようにブロードキャストを待ち受ける
     * */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // サービス起動
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 通知出す
        showNotification()
        // ブロードキャストを登録
        registerBroadcast()

        // NSD（ローカルネットワークから検出できるやつ） と PhoTransferサーバー を起動する
        scope.launch {
            // ポート番号
            val portNumber = dataStore.data.first()[SettingKeyObject.PORT_NUMBER] ?: SettingKeyObject.DEFAULT_PORT_NUMBER
            // ローカルネットワークへ登録
            launch {
                val networkServiceDiscovery = NetworkServiceDiscovery(this@PhoTransferService)
                networkServiceDiscovery.registerService(portNumber).collect { info ->
                    // DataStoreに入れる
                    dataStore.edit { it[SettingKeyObject.SERVER_SIDE_DEVICE_NAME] = info.serviceName }
                    // 通知出し直す
                    showNotification("${getString(R.string.device_name)} : ${info.serviceName}")
                }
            }
            // PhoTransferサーバー起動
            launch {
                val server = PhoTransferServer()
                server.startServer(portNumber, receivePhotoFolder.path).collect { (deviceName, filePath) ->
                    // 保存に成功すると呼ばれるので、MediaStoreへ保存する
                    MediaStoreTool.insertPhoto(this@PhoTransferService, deviceName, File(filePath), true)
                }
            }
        }
    }

    /** ブロードキャストを登録する */
    private fun registerBroadcast() {
        val intentFilter = IntentFilter().apply {
            addAction("service_stop")
        }
        registerReceiver(broadcastReceiver, intentFilter)
    }

    /**
     * フォアグラウンドサービス通知を出す
     * @param contentText 通知に表示する文字
     * */
    private fun showNotification(contentText: String = getString(R.string.running_server)) {
        val channelId = "io.github.takusan23.photransfer.service_notification"
        val notificationChannel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_LOW).apply {
            setName(getString(R.string.server_notification_title))
        }.build()
        // チャンネル登録
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(channelId) == null) {
            notificationManager.createNotificationChannel(notificationChannel)
        }
        // 通知出す
        val notification = NotificationCompat.Builder(this, channelId).apply {
            setContentTitle(getString(R.string.server_notification_title))
            setContentText(contentText)
            setSmallIcon(R.drawable.photransfer_icon)
            // 終了ボタン
            addAction(R.drawable.ic_outline_close_24, getString(R.string.close), PendingIntent.getBroadcast(this@PhoTransferService, 10, Intent("service_stop"), PendingIntent.FLAG_IMMUTABLE))
        }.build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /** サービス終了時 */
    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        unregisterReceiver(broadcastReceiver)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        /**
         * サービスを起動する
         * @param context Context
         * */
        fun startService(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, PhoTransferService::class.java))
        }

        /**
         * サービス終了
         * */
        fun stopService(context: Context) {
            context.stopService(Intent(context, PhoTransferService::class.java))
        }

    }

}