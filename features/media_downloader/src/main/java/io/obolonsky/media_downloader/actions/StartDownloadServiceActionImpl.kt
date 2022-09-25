package io.obolonsky.media_downloader.actions

import android.content.Context
import androidx.media3.exoplayer.offline.DownloadService
import io.obolonsky.core.di.actions.StartDownloadServiceAction
import io.obolonsky.media_downloader.MediaDownloadService
import javax.inject.Inject

internal class StartDownloadServiceActionImpl @Inject constructor() : StartDownloadServiceAction {

    override fun start(context: Context) {
        // Starting the service in the foreground causes notification flicker if there is no scheduled
        // action. Starting it in the background throws an exception if the app is in the background too
        // (e.g. if device screen is locked).
        try {
            DownloadService.start(context, MediaDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(context, MediaDownloadService::class.java)
        }
    }
}