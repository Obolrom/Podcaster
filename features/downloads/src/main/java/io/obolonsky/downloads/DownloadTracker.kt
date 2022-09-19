package io.obolonsky.downloads

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.*
import com.google.common.base.Preconditions
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/** Tracks media that has been downloaded. */
@OptIn(markerClass = [UnstableApi::class])
@FeatureScope
class DownloadTracker @Inject constructor(
    val context: Context,
    downloadManager: DownloadManager,
    private val dataSourceFactory: CacheDataSource.Factory,
    private val dispatchers: CoroutineSchedulers,
    private val inMemoryStorage: InMemoryStorage,
) {

    private val downloads: HashMap<Uri, Download> = HashMap()
    private var downloadIndex: DownloadIndex = downloadManager.downloadIndex

    init {
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    fun isDownloaded(mediaItem: MediaItem): Boolean {
        val download = downloads[Preconditions.checkNotNull(mediaItem.localConfiguration).uri]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun release() {
        downloadHelper?.release()
    }

    fun getDownloadRequest(uri: Uri): DownloadRequest? {
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request
            else null
    }

    private var downloadHelper: DownloadHelper? = null

    fun toggleDownload(
        mediaItem: MediaItem,
        renderersFactory: RenderersFactory?
    ) {
        val download = inMemoryStorage.downloads
            .replayCache
            .firstOrNull()
            ?.firstOrNull {
                it.request.uri == Preconditions.checkNotNull(mediaItem.localConfiguration).uri
            }
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

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun loadDownloads() {
        CoroutineScope(Job()).launch(dispatchers.io) {
            try {
                downloadIndex.getDownloads().use { loadedDownloads ->
                    val downloadList = mutableListOf<Download>()
                    while (loadedDownloads.moveToNext()) {
                        val download = loadedDownloads.download
                        downloadList.add(download)
                        downloads[download.request.uri] = download
                    }
                    inMemoryStorage.mutableDownloads.emit(downloadList)
                }
            } catch (e: IOException) {
                Timber.e(e)
                throw e
            }
        }
    }

    private inner class DownloadCallback : DownloadHelper.Callback {
        override fun onPrepared(helper: DownloadHelper) {
            Timber.d("customDownloads DownloadCallback.onPrepared")
            startDownload()
            loadDownloads()
        }

        override fun onPrepareError(helper: DownloadHelper, e: IOException) {
            Timber.d("customDownloads DownloadCallback.onPrepareError $e")
            loadDownloads()
        }

        private fun startDownload() {
            startDownload(buildDownloadRequest())
        }

        private fun startDownload(downloadRequest: DownloadRequest) {
            DownloadService.sendAddDownload(
                context,
                MediaDownloadService::class.java,
                downloadRequest,
                /* foreground= */false
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
            Timber.d("downloadsStorage $finalException")
            loadDownloads()
            downloads[download.request.uri] = download
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            loadDownloads()
            downloads.remove(download.request.uri)
        }

        override fun onInitialized(downloadManager: DownloadManager) {
            Timber.d("customDownloads DownloadManager.onInitialized")
        }

        override fun onIdle(downloadManager: DownloadManager) {
            Timber.d("customDownloads DownloadManager.onIdle")
        }
    }
}