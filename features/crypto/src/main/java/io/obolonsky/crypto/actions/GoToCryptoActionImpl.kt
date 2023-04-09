package io.obolonsky.crypto.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.GoToCryptoAction
import io.obolonsky.crypto.ui.CryptoActivity
import javax.inject.Inject

class GoToCryptoActionImpl @Inject constructor() : GoToCryptoAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, CryptoActivity::class.java))
    }
}