package io.github.takusan23.photransfer.service

import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import io.github.takusan23.photransfer.R
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import io.github.takusan23.photransfer.tool.IPAddressTool
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

/**
 * クイック設定からサーバーをONにできるように
 * */
class StartQuickSettingTileService : TileService() {

    /** DataStoreはコルーチン必要なので */
    private var coroutineScope: CoroutineScope? = null

    /** 実行しているならtrue */
    private var isRunning = false

    /** サーバーモードならtrue */
    private var isServerMode = true

    /**
     * クイック設定が表示されたら呼ばれる
     *
     * サーバー実行状態を監視する
     * */
    override fun onStartListening() {
        super.onStartListening()
        // 有効、無効の状態はDataStoreに書き込まれるので、Flowで監視する
        coroutineScope = CoroutineScope(Dispatchers.IO).apply {
            launch {
                dataStore.data.collect { setting ->
                    isServerMode = setting[SettingKeyObject.MODE] == SettingKeyObject.MODE_SERVER
                    isRunning = setting[SettingKeyObject.IS_RUNNING] == true
                    val serverName = setting[SettingKeyObject.SERVER_SIDE_DEVICE_NAME]
                    val ipAddress = IPAddressTool.collectIpAddress(this@StartQuickSettingTileService).first()
                    // サーバーモードで実行中ならACTIVE状態へ
                    withContext(Dispatchers.Main) {
                        when {
                            isServerMode && isRunning -> {
                                qsTile.label = serverName
                                updateSubTitle(ipAddress)
                                qsTile.state = Tile.STATE_ACTIVE
                            }
                            isServerMode && !isRunning -> {
                                qsTile.label = getString(R.string.app_name)
                                updateSubTitle(getString(R.string.disable_short_title))
                                qsTile.state = Tile.STATE_INACTIVE
                            }
                            else -> {
                                updateSubTitle("")
                                qsTile.state = Tile.STATE_UNAVAILABLE
                            }
                        }
                        qsTile.updateTile()
                    }
                }
            }
        }
    }

    /**
     * クイック設定が画面外に行ったときに呼ばれる？
     * */
    override fun onStopListening() {
        super.onStopListening()
        coroutineScope?.cancel()
    }

    /**
     * クイック設定のサブタイトルをセットする
     *
     * Android 9未満は何もしない
     *
     * @param subTitle サブタイトル
     * */
    private fun updateSubTitle(subTitle: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            qsTile.subtitle = subTitle
        }
    }

    /**
     * 押したときに呼ばれる
     * */
    override fun onClick() {
        super.onClick()
        // サーバーモードで、キーガード中でも有効にできるように
        if (isServerMode /*&& !isLocked */) {
            // 起動したり終了したり
            if (isRunning) {
                PhoTransferService.stopService(this)
            } else {
                PhoTransferService.startService(this)
            }
        }
    }

    /**
     * 削除したとき
     * */
    override fun onTileRemoved() {
        super.onTileRemoved()
        coroutineScope?.cancel()
    }

}