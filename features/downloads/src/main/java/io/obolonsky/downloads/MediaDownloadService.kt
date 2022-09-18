package io.obolonsky.downloads

import android.app.Notification
import android.content.Context
import androidx.media3.common.util.NotificationUtil
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.offline.Download
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.offline.DownloadNotificationHelper
import androidx.media3.exoplayer.offline.DownloadService
import androidx.media3.exoplayer.scheduler.PlatformScheduler
import androidx.media3.exoplayer.scheduler.Scheduler
import io.obolonsky.downloads.DownloadUtils.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import androidx.media3.exoplayer.R as Media3R

class MediaDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    Media3R.string.exo_download_notification_channel_name,
    CHANNEL_DESCRIPTION_RESOURCE_ID
) {

    override fun getDownloadManager(): DownloadManager {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        val downloadManager: DownloadManager = DownloadUtils.getDownloadManager(this)
        val downloadNotificationHelper = DownloadUtils.getDownloadNotificationHelper(this)
        downloadManager.addListener(
            TerminalStateNotificationHelper(
                this,
                downloadNotificationHelper,
                FOREGROUND_NOTIFICATION_ID + 1
            )
        )
        return downloadManager
    }

    override fun getScheduler(): Scheduler? {
        return when {
            Util.SDK_INT >= 21 -> PlatformScheduler(this, JOB_ID)

            else -> null
        }
    }

    override fun getForegroundNotification(
        downloads: MutableList<Download>,
        notMetRequirements: Int
    ): Notification {
        return DownloadUtils.getDownloadNotificationHelper(this)
            .buildProgressNotification(
                this,
                /* download done */  io.obolonsky.coreui.R.drawable.ic_round_arrow_back_24,
                null,
                null,
                downloads,
                notMetRequirements
            )
    }


    /**
     * Creates and displays notifications for downloads when they complete or fail.
     *
     *
     * This helper will outlive the lifespan of a single instance of [MediaDownloadService].
     * It is static to avoid leaking the first [MediaDownloadService] instance.
     */
    private class TerminalStateNotificationHelper(
        context: Context, notificationHelper: DownloadNotificationHelper, firstNotificationId: Int
    ) :
        DownloadManager.Listener {
        private val context: Context
        private val notificationHelper: DownloadNotificationHelper
        private var nextNotificationId: Int
        override fun onDownloadChanged(
            downloadManager: DownloadManager, download: Download, finalException: Exception?
        ) {
            val notification: Notification = when (download.state) {
                Download.STATE_COMPLETED -> {
                    notificationHelper.buildDownloadCompletedNotification(
                        context,
                      /* download done */  io.obolonsky.coreui.R.drawable.ic_round_arrow_back_24,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                Download.STATE_FAILED -> {
                    notificationHelper.buildDownloadFailedNotification(
                        context,
                        /* download done */  io.obolonsky.coreui.R.drawable.ic_round_arrow_back_24,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                else -> {
                    return
                }
            }
            NotificationUtil.setNotification(context, nextNotificationId++, notification)
        }

        init {
            this.context = context.applicationContext
            this.notificationHelper = notificationHelper
            nextNotificationId = firstNotificationId
        }
    }

    companion object {
        const val FOREGROUND_NOTIFICATION_ID = 1
        const val JOB_ID = 1
        const val FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000L
        const val CHANNEL_DESCRIPTION_RESOURCE_ID = 0
    }
}