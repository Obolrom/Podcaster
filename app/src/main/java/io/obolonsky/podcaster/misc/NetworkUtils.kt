package io.obolonsky.podcaster.misc

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.IntRange

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

private fun Context.isNetworkConnected(connectionType: Int): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager?
    val activeNetworkInfo = connectivityManager?.getNetworkInfo(connectionType)

    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

@IntRange(from = 0, to = 3)
fun getConnectionType(context: Context): Int {
    var result = 0 // Returns connection type. 0: none; 1: mobile data; 2: wifi; 3: vpn
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = 2
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = 1
                } else if (hasTransport(NetworkCapabilities.TRANSPORT_VPN)){
                    result = 3
                }
            }
        }
    } else {
        cm?.run {
            cm.activeNetworkInfo?.run {
                when (type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        result = 2
                    }
                    ConnectivityManager.TYPE_MOBILE -> {
                        result = 1
                    }
                    ConnectivityManager.TYPE_VPN -> {
                        result = 3
                    }
                }
            }
        }
    }
    return result
}