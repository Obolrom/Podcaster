package io.obolonsky.downloads

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.*
import com.google.common.base.Preconditions
import timber.log.Timber
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet

/** Tracks media that has been downloaded. */
@OptIn(markerClass = [UnstableApi::class])
class DownloadTracker(
    val context: Context,
    private val dataSourceFactory: DataSource.Factory,
    downloadManager: DownloadManager,
) {

    /** Listens for changes in the tracked downloads.  */
    interface Listener {

        /** Called when the tracked downloads changed.  */
        fun onDownloadsChanged()
    }

    private val TAG = "DownloadTracker"

    private val listeners: CopyOnWriteArraySet<Listener> = CopyOnWriteArraySet()
    private val downloads: HashMap<Uri, Download> = HashMap()
    private var downloadIndex: DownloadIndex? = null

    init {
        downloadIndex = downloadManager.downloadIndex
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    fun addListener(listener: Listener?) {
        listeners.add(Preconditions.checkNotNull(listener))
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun isDownloaded(mediaItem: MediaItem): Boolean {
        val download = downloads[Preconditions.checkNotNull(mediaItem.localConfiguration).uri]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun getDownloadRequest(uri: Uri): DownloadRequest? {
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request else null
    }

    private var downloadHelper: DownloadHelper? = null

    fun toggleDownload(
        mediaItem: MediaItem,
        renderersFactory: RenderersFactory?
    ) {
        val download = downloads[Preconditions.checkNotNull(mediaItem.localConfiguration).uri]
        if (download != null && download.state != Download.STATE_FAILED) {
            DownloadService.sendRemoveDownload(
                context,
                MediaDownloadService::class.java,
                download.request.id,  /* foreground= */
                false
            )
        } else {
            downloadHelper = DownloadHelper.forMediaItem(
                context,
                mediaItem,
                renderersFactory,
                dataSourceFactory
            ).apply { prepare(DownloadCallback()) }
        }
    }

    private fun loadDownloads() {
        try {
            downloadIndex!!.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    val download =
                        loadedDownloads.download
                    downloads[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to query downloads", e)
        }
    }

    private inner class DownloadCallback : DownloadHelper.Callback {
        override fun onPrepared(helper: DownloadHelper) {
            Timber.d("customDownloads DownloadCallback.onPrepared")
            startDownload()
        }

        override fun onPrepareError(helper: DownloadHelper, e: IOException) {
            Timber.d("customDownloads DownloadCallback.onPrepareError")
        }

        private fun startDownload() {
            startDownload(buildDownloadRequest())
        }

        private fun startDownload(downloadRequest: DownloadRequest) {
            DownloadService.sendAddDownload(
                context, MediaDownloadService::class.java, downloadRequest,  /* foreground= */false
            )
        }

        private fun buildDownloadRequest(): DownloadRequest {
            return downloadHelper
                ?.getDownloadRequest(
                    Util.getUtf8Bytes("test")
                )!!
        }
    }

    private inner class DownloadManagerListener : DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager, download: Download, finalException: Exception?
        ) {
            downloads[download.request.uri] = download
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            downloads.remove(download.request.uri)
            for (listener in listeners) {
                listener.onDownloadsChanged()
            }
        }

        override fun onInitialized(downloadManager: DownloadManager) {
            Timber.d("customDownloads DownloadManager.onInitialized")
        }

        override fun onIdle(downloadManager: DownloadManager) {
            Timber.d("customDownloads DownloadManager.onIdle")
        }
    }
}