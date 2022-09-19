package io.obolonsky.core.di.downloads

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.RenderersFactory
import androidx.media3.exoplayer.offline.DownloadService

interface Downloader {

    fun toggleDownload(
        mediaItem: MediaItem,
        renderersFactory: RenderersFactory?,
        serviceClass: Class<out DownloadService>,
    )

    fun release()
}