package io.obolonsky.storage.di

import io.obolonsky.storage.database.ExoPlayerDao
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.utils.TransactionManager

interface StorageProvider {

    val transactionManager: TransactionManager

    val shazamTrackDao: ShazamTrackDao

    val exoDao: ExoPlayerDao
}