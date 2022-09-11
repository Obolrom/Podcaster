package io.obolonsky.repository.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.core.di.utils.JsonConverter
import io.obolonsky.repository.database.AppDatabase
import io.obolonsky.repository.database.Converter
import io.obolonsky.repository.database.TransactionManager
import io.obolonsky.repository.database.daos.ShazamTrackDao

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
    fun provideTransactionManager(db: AppDatabase): TransactionManager = db

    @Provides
    fun provideShazamTrackDao(db: AppDatabase): ShazamTrackDao = db.shazamTrackDao
}