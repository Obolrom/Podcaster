package io.obolonsky.downloads

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class DownloadUtils @Inject constructor() {

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