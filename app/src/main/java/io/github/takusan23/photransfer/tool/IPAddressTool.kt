package io.github.takusan23.photransfer.tool

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow


object IPAddressTool {

    /**
     * IPアドレスをFlowで流す
     *
     * @param context Context
     * @return IPv4のIPアドレス
     * */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun collectIpAddress(context: Context) = channelFlow {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
        var networkCallback: ConnectivityManager.NetworkCallback? = null
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val linkProperties = connectivityManager.getLinkProperties(network)
                // IPv4アドレスを得る。
                val address = linkProperties?.linkAddresses
                    ?.find { it.address?.toString()?.contains("192") == true }
                    ?.address?.hostAddress
                if (address != null) {
                    trySend(address)
                }
            }
        }
        connectivityManager.registerNetworkCallback(request, networkCallback)
        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

}