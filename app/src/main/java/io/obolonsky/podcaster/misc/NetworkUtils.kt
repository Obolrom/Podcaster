package io.obolonsky.podcaster.misc

import android.content.Context
import android.net.ConnectivityManager

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