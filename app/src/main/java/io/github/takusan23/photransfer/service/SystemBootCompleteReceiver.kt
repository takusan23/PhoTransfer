package io.github.takusan23.photransfer.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * デバイス起動ブロードキャスト「android.intent.action.BOOT_COMPLETED」を受け取るブロードキャストレシーバー
 * */
class SystemBootCompleteReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        runBlocking {
            // モードがサーバーならサーバーサービス起動
            val isServer = context.dataStore.data.first()[SettingKeyObject.MODE] == SettingKeyObject.MODE_SERVER
            if (isServer) {
                // 自動起動
                PhoTransferService.startService(context)
            }
        }
    }

}