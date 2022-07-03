package io.obolonsky.podcaster.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.obolonsky.podcaster.data.room.PodcasterDatabase
import io.obolonsky.podcaster.data.room.daos.SongDao
import io.obolonsky.podcaster.di.scopes.ApplicationScope

@Module
class DatabaseModule {

    @ApplicationScope
    @Provides
    fun provideDb(appCtx: Context): PodcasterDatabase {
        return Room.databaseBuilder(
            appCtx,
            PodcasterDatabase::class.java, DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @ApplicationScope
    @Provides
    fun provideSongDao(db: PodcasterDatabase): SongDao = db.getSongDao()

    companion object {
        private const val DATABASE_NAME = "podcaster_db"
    }
}