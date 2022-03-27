package io.obolonsky.podcaster.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDb(@ApplicationContext applicationContext: Context): PodcasterDatabase {
        return Room.databaseBuilder(
            applicationContext,
            PodcasterDatabase::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideSongDao(db: PodcasterDatabase): SongDao = db.getSongDao()

    companion object {
        private const val DATABASE_NAME = "podcaster_db"
    }
}