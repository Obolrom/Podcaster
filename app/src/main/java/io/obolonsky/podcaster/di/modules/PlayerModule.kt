package io.obolonsky.podcaster.di.modules

import android.content.Context
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.ApplicationScope

@Module
class PlayerModule {

    // TODO: USE SERVICE_SCOPE INSTEAD OF APP SCOPE
    @ApplicationScope
    @Provides
    fun provideDefaultTrackSelector(
        applicationContext: Context
    ): DefaultTrackSelector {
        val trackSelector = DefaultTrackSelector(applicationContext)
        val parameters = DefaultTrackSelector.ParametersBuilder(applicationContext)
            .build()

        trackSelector.parameters = parameters

        return trackSelector
    }

    @ApplicationScope
    @Provides
    fun provideLoadControl(): DefaultLoadControl {
        return DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs */ 45_000,
                /* maxBufferMs */ 50_000,
                /* bufferForPlaybackMs */ 15_000,
                /* bufferForPlaybackAfterRebufferMs */ 30_000,
            )
            .setBackBuffer(
                /* backBufferDurationMs */ 90_000,
                /* retainBackBufferFromKeyframe */true)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideSimpleExoPlayer(
        applicationContext: Context,
        trackSelector: DefaultTrackSelector,
        loadControl: DefaultLoadControl,
        audioAttributes: AudioAttributes,
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(applicationContext)
            .setTrackSelector(trackSelector)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setLoadControl(loadControl)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideDataSourceFactory(
        context: Context
    ) = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Podcaster"))
}