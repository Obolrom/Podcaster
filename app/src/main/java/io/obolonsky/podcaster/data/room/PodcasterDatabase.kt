package io.obolonsky.podcaster.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.data.room.entities.Song

@Database(entities = [
        Song::class,
    ],
    exportSchema = false,
    version = 1
)
abstract class PodcasterDatabase: RoomDatabase() {

    abstract fun getSongDao(): SongDao

}