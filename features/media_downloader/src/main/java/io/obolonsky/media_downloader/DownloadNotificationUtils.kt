package io.obolonsky.media_downloader

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadNotificationHelper

internal class DownloadNotificationUtils {

    private var downloadNotificationHelper: DownloadNotificationHelper? = null

    @OptIn(markerClass = [UnstableApi::class])
    @Synchronized
    fun getDownloadNotificationHelper(
        context: Context
    ): DownloadNotificationHelper {
        return downloadNotificationHelper ?: run {
            DownloadNotificationHelper(context, DOWNLOAD_NOTIFICATION_CHANNEL_ID).also {
                downloadNotificationHelper = it
            }
        }
    }

    companion object {
        const val DOWNLOAD_NOTIFICATION_CHANNEL_ID = "download_channel"
    }
}