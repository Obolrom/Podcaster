package io.obolonsky.repository.database.di

import io.obolonsky.repository.database.TransactionManager
import io.obolonsky.repository.database.daos.ShazamTrackDao

interface DatabaseComponentProvider {

    val transactionManager: TransactionManager

    val shazamTrackDao: ShazamTrackDao
}