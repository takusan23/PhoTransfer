package io.github.takusan23.photransfer.tool

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow

object NetworkCheckTool {

    /**
     * Wi-Fiネットワークに接続中かどうか
     *
     * @param context Context
     * @return Wi-Fiならtrue
     * */
    fun isConnectionWiFi(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    /**
     * Wi-Fi接続を監視する。
     *
     * @param context Context
     * @return trueならWiFi接続あります
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun listenWiFiConnection(context: Context) = channelFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Wi-Fi接続した
                trySend(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Wi-Fi切れた
                trySend(false)
            }
        }
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

}