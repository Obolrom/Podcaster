package io.obolonsky.podcaster.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.NetworkStatus
import io.obolonsky.core.di.utils.NetworkStatusObservable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ApplicationScope
class NetworkStatusObservableImpl @Inject constructor(
    context: Context,
) : NetworkStatusObservable {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val statusFlow = callbackFlow {
        val connectivityCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                launch { send(NetworkStatus.AVAILABLE) }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                launch { send(NetworkStatus.LOSING) }
            }

            override fun onLost(network: Network) {
                launch { send(NetworkStatus.LOST) }
            }

            override fun onUnavailable() {
                launch { send(NetworkStatus.UNAVAILABLE) }
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, connectivityCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(connectivityCallback)
        }
    }
}