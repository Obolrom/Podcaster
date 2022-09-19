package io.obolonsky.core.di.downloads.providers

import androidx.media3.datasource.DataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.offline.DownloadManager
import io.obolonsky.core.di.downloads.DownloadsStorage

interface DownloadsStorageProvider : DownloaderProvider {

    val downloadsStorage: DownloadsStorage

    /**
     * [Cache.release] should be called, when no necessary
     */
    val exoCache: Cache

    val httpDataSourceFactory: DataSource.Factory

    val cacheDataSourceFactory: CacheDataSource.Factory

    val downloadManager: DownloadManager
}