package io.obolonsky.podcaster.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.obolonsky.podcaster.player.MusicServiceConnection
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideMusicServiceConnection(
        @ApplicationContext context: Context
    ) = MusicServiceConnection(context)

    @Singleton
    @Provides
    fun provideDispatchers() = object : CoroutineSchedulers {

        override val main = kotlinx.coroutines.Dispatchers.Main

        override val io = kotlinx.coroutines.Dispatchers.IO

        override val computation = kotlinx.coroutines.Dispatchers.Default

        override val unconfined = kotlinx.coroutines.Dispatchers.Unconfined
    }
}