package io.obolonsky.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import io.obolonsky.repository.database.daos.ShazamTrackDao
import io.obolonsky.repository.database.entities.ShazamTrack

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