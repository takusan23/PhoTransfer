package io.github.takusan23.photransfer.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

object ChargingCheckTool {

    /**
     * 充電状態を監視する
     *
     * サービス等のonDestroyでコルーチンスコープを終了するようにしてください。
     *
     * onDestroyで終了させる場合は、runBlockingとか使うかも？
     *
     * @param context Context
     * @return 充電中ならtrue、そうじゃないならfalse
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun listenCharging(context: Context) = callbackFlow {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intentFilter = IntentFilter().apply {
            addAction(BatteryManager.ACTION_CHARGING)
            addAction(BatteryManager.ACTION_DISCHARGING)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                trySend(batteryManager.isCharging)
            }
        }
        // 登録
        context.registerReceiver(receiver, intentFilter)
        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }

    /**
     * 充電中ならtrueを返す
     * @param context Context
     * @return 充電中ならtrue
     * */
    fun isCharging(context: Context) = (context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager).isCharging

}