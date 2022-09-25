package io.obolonsky.core.di.downloads.providers

import androidx.media3.exoplayer.offline.DownloadManager
import io.obolonsky.core.di.downloads.DownloadsStorage
import io.obolonsky.core.di.player.PlayerDependenciesProvider

interface DownloadsStorageProvider : DownloaderProvider, PlayerDependenciesProvider {

    val downloadsStorage: DownloadsStorage

    val downloadManager: DownloadManager
}