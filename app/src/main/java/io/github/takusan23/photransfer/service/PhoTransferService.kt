package io.github.takusan23.photransfer.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.edit
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.network.AlternativeNSD
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.tool.ChargingCheckTool
import io.github.takusan23.photransfer.tool.MediaStoreTool
import io.github.takusan23.photransfer.tool.NetworkCheckTool
import io.github.takusan23.server.PhoTransferServer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.File

/**
 * サーバー側（写真受け取り側）は常に受付をするのでサービズで実行
 * */
class PhoTransferService : Service() {

    /** 通知マネージャー */
    private val notificationManager by lazy { NotificationManagerCompat.from(this) }

    /** コルーチンスコープ */
    private val scope = CoroutineScope(Dispatchers.Main)

    /** 一時保存先。
     *
     * ここに保存したあとMediaStoreに登録して、端末のギャラリーアプリから見れるようにする。
     *
     * ギャラリーから見れるようになればGoogleフォトのメディアスキャンに引っかかるはず。
     * */
    private val receivePhotoFolder by lazy { File(externalCacheDir, "receive_photo").apply { mkdir() } }

    /** ローカルネットワークへ登録するやつとサーバーを起動してるコルーチンのJob */
    private var serverJob: Job? = null

    /**
     * 通知を消せるようにブロードキャストを待ち受ける
     * */
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Wi-Fi切り替え
            when (intent?.action) {
                "io.github.takusan23.photransfer.photransfer_service_stop" -> {
                    // サービス終了
                    stopSelf()
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 通知出す
        showNotification(getString(R.string.init_server))
        // ブロードキャストを登録
        registerBroadcast()
        // ネットワーク状態監視
        registerNetworkListener()
        // サービス起動
        scope.launch { dataStore.edit { it[SettingKeyObject.IS_RUNNING] = true } }
    }

    /**
     * WakeLockに登録する
     *
     * コルーチンでキャンセルされるはず
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun registerWakeLock() {
        scope.launch {
            callbackFlow<Unit> {
                val wakeLock: PowerManager.WakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PhoTransfer::ServiceWakeLockTag").apply {
                        acquire()
                    }
                }
                awaitClose { wakeLock.release() }
            }.collect()
        }
    }

    /** Wi-Fiに接続されているか監視する */
    @OptIn(InternalCoroutinesApi::class)
    private fun registerNetworkListener() {
        scope.launch {

            // 充電中のみ？
            val isRequireCharging = dataStore.data.first()[SettingKeyObject.SETTING_TRANSFER_REQUIRE_CHARGING] ?: true
            // 充電Flow
            val chargingFlow = if (isRequireCharging) ChargingCheckTool.listenCharging(this@PhoTransferService) else emptyFlow()
            // Wi-Fi検知Flow
            val wifiConnectionFlow = NetworkCheckTool.listenWiFiConnection(this@PhoTransferService)

            // Flowを連結させる
            merge(chargingFlow, wifiConnectionFlow).collect {
                //  println("あっぷでーと：${SimpleDateFormat("HH:mm:ss").format(System.currentTimeMillis())}")
                val isWiFiConnecting = NetworkCheckTool.isConnectionWiFi(this@PhoTransferService)
                // 充電中か、isRequireChargingがfalseなら無条件true
                val isCharging = if (isRequireCharging) ChargingCheckTool.isCharging(this@PhoTransferService) else true

                if (isWiFiConnecting && isCharging) {
                    // サーバーとローカルネットワークから検出可能にする
                    startServer()
                } else {
                    // 使えない理由など。Because of ねぇ気付いてた？  並んで歩くとき
                    val becauseText = arrayListOf<String>()
                    if (!isWiFiConnecting) becauseText += getString(R.string.wait_wifi_connection)
                    if (!isCharging) becauseText += getString(R.string.wait_battery_charging)
                    shutdownServer(becauseText.joinToString(separator = "\n"))
                }
            }
        }
    }

    /**
     * サーバー起動
     *
     * ローカルネットワークに追加とPhoTransferサーバーを立ち上げる
     * */
    private fun startServer() {
        // NSD（ローカルネットワークから検出できるやつ） と PhoTransferサーバー を起動する
        serverJob = scope.launch {
            // ポート番号
            val portNumber = dataStore.data.first()[SettingKeyObject.PORT_NUMBER] ?: SettingKeyObject.DEFAULT_PORT_NUMBER
            // ローカルから見つかるように
            launch {
                val alternativeNSD = AlternativeNSD(this@PhoTransferService)
                alternativeNSD.registerAltNSD(portNumber).collect { serviceName ->
                    // DataStoreに入れる
                    dataStore.edit { it[SettingKeyObject.SERVER_SIDE_DEVICE_NAME] = serviceName }
                    // 通知出し直す
                    showNotification("${getString(R.string.device_name)} : ${serviceName}")
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

    /**
     * サーバーをシャットダウンする。サービスはそのまま
     * @param serviceInfoText Wi-Fi接続を待機中など
     * */
    private fun shutdownServer(serviceInfoText: String = getString(R.string.wait_wifi_connection)) {
        serverJob?.cancelChildren()
        showNotification(serviceInfoText)
    }

    /** ブロードキャストを登録する */
    private fun registerBroadcast() {
        val intentFilter = IntentFilter().apply {
            addAction("io.github.takusan23.photransfer.photransfer_service_stop")
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
            addAction(R.drawable.ic_outline_close_24, getString(R.string.close), PendingIntent.getBroadcast(this@PhoTransferService, 10, Intent("io.github.takusan23.photransfer.photransfer_service_stop"), PendingIntent.FLAG_IMMUTABLE))
        }.build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    /** サービス終了時 */
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        runBlocking {
            dataStore.edit { it[SettingKeyObject.IS_RUNNING] = false }
            dataStore.edit { it[SettingKeyObject.SERVER_SIDE_DEVICE_NAME] = "---" }
            scope.cancel()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        /** 通知のID */
        const val NOTIFICATION_ID = 1030

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