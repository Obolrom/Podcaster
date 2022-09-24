package io.obolonsky.storage.downloads

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.*
import com.google.common.base.Preconditions
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.downloads.Downloader
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ApplicationScope
internal class DownloaderImpl @Inject constructor(
    val context: Context,
    downloadManager: DownloadManager,
    private val dataSourceFactory: CacheDataSource.Factory,
    private val renderersFactory: RenderersFactory,
    private val dispatchers: CoroutineSchedulers,
    private val inMemoryStorage: DownloadsStorageImpl,
) : Downloader {

    private val downloadCoroutineScope = CoroutineScope(SupervisorJob())
    private val downloads: HashMap<Uri, Download> = HashMap()
    private val downloadIndex: DownloadIndex = downloadManager.downloadIndex
    private var downloadHelper: DownloadHelper? = null

    init {
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    override fun toggleDownload(
        mediaItem: MediaItem,
        serviceClass: Class<out DownloadService>,
    ) {
        val download = inMemoryStorage.downloads
            .replayCache
            .firstOrNull()
            ?.let { reaction ->
                when (reaction) {
                    is Reaction.Success -> {
                        reaction.data.firstOrNull() {
                            it.request.uri ==
                                    Preconditions.checkNotNull(mediaItem.localConfiguration).uri
                        }
                    }
                    is Reaction.Fail -> {
                        null
                    }
                }
            }
        if (download != null && download.state != Download.STATE_FAILED) {
            DownloadService.sendRemoveDownload(
                context,
                serviceClass,
                download.request.id,  /* foreground= */
                false
            )
        } else {
            downloadHelper = DownloadHelper.forMediaItem(
                context,
                mediaItem,
                renderersFactory,
                dataSourceFactory
            ).apply { prepare(DownloadCallback(serviceClass)) }
        }
    }

    override fun release() {
        downloadHelper?.release()
    }

    fun isDownloaded(mediaItem: MediaItem): Boolean {
        val download = downloads[Preconditions.checkNotNull(mediaItem.localConfiguration).uri]
        return download != null && download.state != Download.STATE_FAILED
    }

    fun getDownloadRequest(uri: Uri): DownloadRequest? {
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request
        else null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun loadDownloads() {
        downloadCoroutineScope.launch(dispatchers.io) {
            try {
                downloadIndex.getDownloads().use { loadedDownloads ->
                    val downloadList = mutableListOf<Download>()
                    while (loadedDownloads.moveToNext()) {
                        val download = loadedDownloads.download
                        downloadList.add(download)
                        downloads[download.request.uri] = download
                    }
                    inMemoryStorage.mutableDownloads
                        .emit(Reaction.Success(downloadList))
                }
            } catch (e: IOException) {
                // TODO: fix error type
                Timber.e(e)
                inMemoryStorage.mutableDownloads
                    .emit(Reaction.Fail(Error.NetworkError(e)))
                throw e
            }
        }
    }

    private inner class DownloadCallback(
        private val serviceClass: Class<out DownloadService>,
    ) : DownloadHelper.Callback {
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
                serviceClass,
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