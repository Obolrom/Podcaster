package io.obolonsky.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.entities.ShazamTrack
import io.obolonsky.storage.database.utils.Converter
import io.obolonsky.storage.database.utils.TransactionManager

@Database(
    entities = [
        ShazamTrack::class,
    ],
    version = 1,
)
@TypeConverters(
    Converter::class,
)
abstract class AppDatabase : RoomDatabase(), TransactionManager {

    abstract val shazamTrackDao: ShazamTrackDao

    override suspend fun runTransaction(transaction: () -> Unit) {
        withTransaction(transaction)
    }
}