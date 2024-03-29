package io.obolonsky.media_downloader

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
import io.obolonsky.core.di.depsproviders.App
import io.obolonsky.media_downloader.DownloadNotificationUtils.Companion.DOWNLOAD_NOTIFICATION_CHANNEL_ID
import io.obolonsky.media_downloader.di.MediaDownloadsComponent
import javax.inject.Inject

class MediaDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    DOWNLOAD_NOTIFICATION_CHANNEL_ID,
    androidx.media3.exoplayer.R.string.exo_download_notification_channel_name,
    CHANNEL_DESCRIPTION_RESOURCE_ID
) {

    @Inject
    internal lateinit var downloadManager: DownloadManager

    private val downloadNotificationUtils by lazy { DownloadNotificationUtils() }

    override fun onCreate() {
        MediaDownloadsComponent
            .create((applicationContext as App).getAppComponent())
            .inject(this)
        super.onCreate()
    }

    override fun getDownloadManager(): DownloadManager {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        downloadManager.addListener(
            TerminalStateNotificationHelper(
                this,
                downloadNotificationUtils.getDownloadNotificationHelper(this),
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
        return downloadNotificationUtils
            .getDownloadNotificationHelper(this)
            .buildProgressNotification(
                this,
                R.drawable.ic_round_download_done_24,
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
        private val context: Context,
        private val notificationHelper: DownloadNotificationHelper,
        firstNotificationId: Int
    ) : DownloadManager.Listener {
        private var nextNotificationId = firstNotificationId

        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            val notification: Notification = when (download.state) {
                Download.STATE_COMPLETED -> {
                    notificationHelper.buildDownloadCompletedNotification(
                        context,
                        R.drawable.ic_round_download_done_24,
                        null,
                        Util.fromUtf8Bytes(download.request.data)
                    )
                }
                Download.STATE_FAILED -> {
                    notificationHelper.buildDownloadFailedNotification(
                        context,
                        R.drawable.ic_round_download_done_24,
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
    }

    companion object {

        const val FOREGROUND_NOTIFICATION_ID = 1
        const val JOB_ID = 1
        const val FOREGROUND_NOTIFICATION_UPDATE_INTERVAL = 1000L
        const val CHANNEL_DESCRIPTION_RESOURCE_ID = 0
    }
}