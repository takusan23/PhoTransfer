package io.github.takusan23.photransfer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.takusan23.photransfer.service.PhoTransferService
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
            // モードがサーバーで、有効時なら起動
            val data = context.dataStore.data.first()
            val isServer = data[SettingKeyObject.MODE] == SettingKeyObject.MODE_SERVER
            val isEnable = data[SettingKeyObject.IS_RUNNING] == true
            if (isServer && isEnable) {
                // 自動起動
                PhoTransferService.startService(context)
            }
        }
    }

}