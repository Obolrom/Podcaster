package io.obolonsky.storage.di

import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.utils.TransactionManager

interface StorageProvider : DownloadsStorageProvider {

    val transactionManager: TransactionManager

    val shazamTrackDao: ShazamTrackDao
}