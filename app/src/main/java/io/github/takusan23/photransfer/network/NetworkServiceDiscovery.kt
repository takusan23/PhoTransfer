package io.github.takusan23.photransfer.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.datastore.preferences.core.edit
import io.github.takusan23.client.PhoTransferClient
import io.github.takusan23.photransfer.data.ServerInfoData
import io.github.takusan23.photransfer.setting.SettingKeyObject
import io.github.takusan23.photransfer.setting.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * android.net.nsdパッケージを使ってmDNSまがいなことをする
 *
 * サービスタイプは「_photransfer._tcp」です。
 *
 * DNS-SDが使える場合（WindowsならAppleのBonjourSDKで使える？）
 *
 * dns-sd -B _photransfer._tcp
 *
 * でこのアプリ（PhoTransfer）が発見できます。
 *
 * Flowを返す関数はちゃんとコルーチンの後始末お願いします（lifecycleScopeを使えばおｋ）
 * */
class NetworkServiceDiscovery(private val context: Context) {

    /** NSDに登録しているサービスタイプ */
    private val SERVICE_TYPE = "_photransfer._tcp"

    /** NSDマネージャー */
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    /** サービス名。PhoTransfer + 端末の名前が入る */
    private val SERVICE_NAME = "PhoTransfer_${Build.MODEL}".replace(" ", "_")

    /**
     * ローカルネットワークに登録する
     *
     * @param port ポート番号
     * @return Flowを返します。流れてくる文字列は、検出時に表示される名前になります
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun registerService(port: Int) = channelFlow {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = SERVICE_NAME
            serviceType = SERVICE_TYPE // プロトコルとトランスポート層を指定？
            setPort(port)
        }
        // コールバック
        val registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                // 登録成功
                if (serviceInfo != null) trySend(serviceInfo)
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {

            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {

            }

            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {

            }
        }
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        // Flow終了時に登録解除する
        // awaitClose { nsdManager.unregisterService(registrationListener) }
    }

    /**
     * ネットワーク検出を始めるが、前回のIPアドレスに変わらず存在する場合は前回の接続情報を返す（そっちのほうが早い）
     *
     * @return Flowを返します。ネットワークサービス詳細が流れてきます。
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun findServerOrGetLatestServer() = callbackFlow {
        var discoveryListener: NsdManager.DiscoveryListener? = null
        val setting = context.dataStore.data.first()
        val latestPortNumber = setting[SettingKeyObject.CLIENT_LATEST_SERVER_PORT_NUMBER]
        val latestAddress = setting[SettingKeyObject.CLIENT_LATEST_SERVER_IP_ADDRESS]
        val latestDeviceName = setting[SettingKeyObject.CLIENT_LATEST_SERVER_DEVICE_NAME]
        if (latestDeviceName != null && latestAddress != null && latestPortNumber != null && PhoTransferClient.checkIsServerLive(latestAddress, latestPortNumber)) {
            // 生きていれば前回のままでOK
            trySend(ServerInfoData(latestDeviceName, latestAddress, latestPortNumber, true))
        } else {
            // 無いので見つける
            discoveryListener = object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {

                }

                override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {

                }

                override fun onDiscoveryStarted(serviceType: String?) {
                    // 検出開始
                }

                override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                    // ネットワークデバイス発見ｗｗｗｗ。接続してIPアドレスとポート番号を取得
                    nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {

                        }

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                            if (serviceInfo == null && serviceInfo?.host?.hostAddress == null) return
                            trySend(ServerInfoData(
                                deviceName = serviceInfo.serviceName,
                                hostAddress = serviceInfo.host.hostAddress!!,
                                portNumber = serviceInfo.port,
                                isLatestServerData = false
                            ))
                            launch {
                                // 保存する
                                context.dataStore.edit {
                                    it[SettingKeyObject.CLIENT_LATEST_SERVER_DEVICE_NAME] = serviceInfo.serviceName
                                    it[SettingKeyObject.CLIENT_LATEST_SERVER_IP_ADDRESS] = serviceInfo.host.hostAddress!!
                                    it[SettingKeyObject.CLIENT_LATEST_SERVER_PORT_NUMBER] = serviceInfo.port
                                }
                            }
                        }
                    })
                }

                override fun onDiscoveryStopped(serviceType: String?) {

                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                    trySend(null)
                }
            }
            // 登録
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        }
        // Flow終了時に登録解除する
        awaitClose { discoveryListener?.also { nsdManager.stopServiceDiscovery(it) } }
    }

    /**
     * ネットワーク検出を始める。見失った場合はnullをFlowに流します
     *
     * @return Flowを返します。ネットワークサービス詳細が流れてきます。
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun findDevice() = channelFlow {
        // 検出コールバック
        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {

            }

            override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {

            }

            override fun onDiscoveryStarted(serviceType: String?) {
                // 検出開始
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                // ネットワークデバイス発見ｗｗｗｗ。接続してIPアドレスとポート番号を取得
                nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {

                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                        if (serviceInfo == null && serviceInfo?.host?.hostAddress == null) return
                        trySend(ServerInfoData(
                            deviceName = serviceInfo.serviceName,
                            hostAddress = serviceInfo.host.hostAddress!!,
                            portNumber = serviceInfo.port,
                            isLatestServerData = false
                        ))
                    }
                })
            }

            override fun onDiscoveryStopped(serviceType: String?) {

            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                trySend(null)
            }
        }
        // 登録
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        // Flow終了時に登録解除する
        awaitClose { nsdManager.stopServiceDiscovery(discoveryListener) }
    }

}