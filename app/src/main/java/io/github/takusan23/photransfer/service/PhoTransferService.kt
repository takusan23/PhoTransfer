package io.github.takusan23.photransfer.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class PhoTransferService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

}