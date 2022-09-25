package io.obolonsky.downloads.actions

import android.content.Context
import android.content.Intent
import io.obolonsky.core.di.actions.NavigateToDownloadsAction
import io.obolonsky.downloads.ui.DownloadsActivity
import javax.inject.Inject

class NavigateToDownloadsActionImpl @Inject constructor() : NavigateToDownloadsAction {

    override fun navigate(context: Context) {
        context.startActivity(Intent(context, DownloadsActivity::class.java))
    }
}