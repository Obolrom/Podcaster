package io.obolonsky.podcaster.misc

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.NavigateToExoPlayerAction
import io.obolonsky.downloads.PlayerActivity
import javax.inject.Inject

class NavigateToExoPlayerActionImpl @Inject constructor() : NavigateToExoPlayerAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, PlayerActivity::class.java))
    }
}