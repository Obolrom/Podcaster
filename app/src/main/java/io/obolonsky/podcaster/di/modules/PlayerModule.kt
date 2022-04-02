package io.obolonsky.podcaster.di.modules

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PlayerModule {

    @Singleton
    @Provides
    fun provideDefaultTrackSelector(
        @ApplicationContext applicationContext: Context
    ): DefaultTrackSelector {
        val trackSelector = DefaultTrackSelector(applicationContext)
        val parameters = DefaultTrackSelector.ParametersBuilder(applicationContext)
            .build()

        trackSelector.parameters = parameters

        return trackSelector
    }

    @Singleton
    @Provides
    fun provideLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
    }

    @Provides
    fun provideSimpleExoPlayer(
        @ApplicationContext applicationContext: Context,
        trackSelector: DefaultTrackSelector,
        loadControl: DefaultLoadControl,
        audioAttributes: AudioAttributes,
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(applicationContext)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .setLoadControl(loadControl)
            .build()
    }
}