package io.github.takusan23.photransfer.tool

import android.content.Context
import androidx.work.*
import io.github.takusan23.photransfer.work.PhotoTransferWorker
import java.util.concurrent.TimeUnit

object WorkManagerTool {

    /** WorkManagerに登録する際に付けてるタグ */
    private val TAG = "io.github.takusan23.photransfer.transfer_work"

    /**
     * 一回だけ転送タスクを実行する
     *
     * @param context Context
     * */
    fun oneShot(context: Context) {
        val transferWork = OneTimeWorkRequestBuilder<PhotoTransferWorker>()
            .addTag(TAG)
            .build()
        val workManager = WorkManager.getInstance(context)
        workManager.enqueue(transferWork)
    }

    /**
     * 定期実行登録を行う
     *
     * Wi-Fi接続時に1時間ごとに実行される
     *
     * @param context Context
     * @param isRequireCharging 充電中のみ行う場合はtrue
     * @param intervalMinute 定期実行間隔。単位は分。最低値は15分以上である必要があります。
     * */
    fun registerRepeat(context: Context, isRequireCharging: Boolean = false, intervalMinute: Long = 60) {
        val workManager = WorkManager.getInstance(context)
        // 既存の定期実行はキャンセル
        unRegisterRepeat(context)
        // 登録
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(isRequireCharging)
            .build()
        val transferWork = PeriodicWorkRequestBuilder<PhotoTransferWorker>(
            intervalMinute, TimeUnit.MINUTES, // 1時間感覚。ダイナモ感覚ダイナモ感覚YOYOYO YEAR!
            10, TimeUnit.MINUTES // 1時間になる10分前に実行する
        )
            .addTag(TAG)
            .setConstraints(constraints)
            .build()
        workManager.enqueue(transferWork)
    }

    /**
     * 定期実行を解除する
     *
     * @param context Context
     * */
    fun unRegisterRepeat(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWorkByTag(TAG)
    }

}