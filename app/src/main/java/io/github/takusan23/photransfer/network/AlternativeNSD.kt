package io.github.takusan23.photransfer.network

import android.content.Context
import android.os.Build
import com.github.druk.dnssd.DNSSDEmbedded
import com.github.druk.dnssd.DNSSDRegistration
import com.github.druk.dnssd.DNSSDService
import com.github.druk.dnssd.RegisterListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow

class AlternativeNSD(private val context: Context) {

    /** NSDに登録しているサービスタイプ */
    private val SERVICE_TYPE = "_photransfer._tcp"

    /** サービス名。PhoTransfer + 端末の名前が入る */
    private val SERVICE_NAME = "PhoTransfer_${Build.MODEL}".replace(" ", "_")

    /** ライブラリに実装されたやつ */
    private val dnssd by lazy { DNSSDEmbedded(context) }

    /**
     * ローカルネットワーク上からこのサーバーを検出できるようにする。
     *
     * サーバーへアクセスするためにIPアドレスを利用するが、そのIPアドレスが動的に変わるので（固定化しない場合）、接続前にIPアドレスを探しに行く
     *
     * そのために使われる。サービス名を設定しておけば一致したネットワークデバイスを見つけられるようになる。
     *
     * @param port ポート番号
     * @return Flowを返します。流れてくる文字列は、検出時に表示される名前になります
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun registerAltNSD(port: Int) = channelFlow {
        val service = dnssd.register(SERVICE_NAME, SERVICE_TYPE, port, object : RegisterListener {
            override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                // コケたら
                println("えらー：$errorCode")
            }

            override fun serviceRegistered(registration: DNSSDRegistration?, flags: Int, serviceName: String?, regType: String?, domain: String?) {
                // 成功したら
                if (serviceName != null) trySend(serviceName)
            }
        })
        awaitClose { service.stop() }
    }

}