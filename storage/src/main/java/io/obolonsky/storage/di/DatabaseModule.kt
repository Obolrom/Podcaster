package io.obolonsky.storage.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.storage.database.AppDatabase
import io.obolonsky.storage.database.ExoDatabase
import io.obolonsky.storage.database.utils.Converter
import io.obolonsky.storage.database.utils.TransactionManager
import io.obolonsky.storage.database.daos.ShazamTrackDao

@Module
class DatabaseModule {

    @ApplicationScope
    @Provides
    fun provideDatabase(
        appCtx: Context,
        jsonConverter: JsonConverter
    ): AppDatabase {
        return Room.databaseBuilder(
            appCtx,
            AppDatabase::class.java,
            "podcaster_db"
        )
            .addTypeConverter(Converter(jsonConverter))
            .fallbackToDestructiveMigration()
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideExoDatabase(
        appCtx: Context,
    ): ExoDatabase {
        return Room.databaseBuilder(
            appCtx,
            ExoDatabase::class.java,
            "exoplayer_internal.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideExoDao(db: ExoDatabase) = db.exoDao

    @ApplicationScope
    @Provides
    fun provideTransactionManager(db: AppDatabase): TransactionManager = db

    @Reusable
    @Provides
    fun provideShazamTrackDao(db: AppDatabase): ShazamTrackDao = db.shazamTrackDao
}