package io.obolonsky.podcaster.di.modules

import android.content.Context
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Singleton
    @Provides
    fun provideDefaultTrackSelector(
        @ApplicationContext applicationContext: Context
    ): DefaultTrackSelector {
        val trackSelector = DefaultTrackSelector(applicationContext)

        trackSelector.setParameters(trackSelector.buildUponParameters())

        return trackSelector
    }

    @Singleton
    @Provides
    fun provideLoadControl(): DefaultLoadControl {
        return DefaultLoadControl()
    }

    @Provides
    fun provideSimpleExoPlayer(
        @ApplicationContext applicationContext: Context,
        trackSelector: DefaultTrackSelector,
        loadControl: DefaultLoadControl,
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(applicationContext)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
    }

}