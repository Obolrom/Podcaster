package io.obolonsky.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import io.obolonsky.utils.enums.NetworkType

val Context.isWifiConnected: Boolean
    get() = isNetworkConnected(ConnectivityManager.TYPE_WIFI)

val Context.isMobileConnected: Boolean
    get() = isNetworkConnected(ConnectivityManager.TYPE_MOBILE)

val Context.isNetworkConnected: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager?
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo

        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

fun Context.getConnectionType(): NetworkType {
    var result = NetworkType.NONE
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = NetworkType.WIFI
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = NetworkType.MOBILE_DATA
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                    result = NetworkType.VPN
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                when (type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        result = NetworkType.WIFI
                    }
                    ConnectivityManager.TYPE_MOBILE -> {
                        result = NetworkType.MOBILE_DATA
                    }
                    ConnectivityManager.TYPE_VPN -> {
                        result = NetworkType.VPN
                    }
                }
            }
        }
    }
    return result
}

private fun Context.isNetworkConnected(connectionType: Int): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.getNetworkInfo(connectionType)

    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}