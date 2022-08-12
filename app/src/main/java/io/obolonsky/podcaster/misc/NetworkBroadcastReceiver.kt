package io.obolonsky.podcaster.misc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class NetworkBroadcastReceiver(
    private val onConnectionChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    private val networkIntentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    override fun onReceive(context: Context?, intent: Intent?) {
        onConnectionChanged(context?.isNetworkConnected == true)
    }

    fun registerReceiver(context: Context) {
        context.registerReceiver(this, networkIntentFilter)
    }

    fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(this)
    }
}