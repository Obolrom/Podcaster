package io.obolonsky.podcaster.di.modules

import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(app: PodcasterApp): PodcasterDatabase {
        return Room.databaseBuilder(app, PodcasterDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideSongDao(db: PodcasterDatabase): SongDao = db.getSongDao()

    companion object {
        private const val DATABASE_NAME = "podcaster_db"
    }
}