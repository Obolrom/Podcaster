package io.obolonsky.player_feature.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.player_feature.R
import java.util.concurrent.TimeUnit

@Module
class PlayerModule {

    @Provides
    @FeatureScope
    fun provideTrackSelector(
        context: Context,
    ): TrackSelector {
        return DefaultTrackSelector(context)
    }

    @Provides
    @FeatureScope
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
    }

    @Provides
    @FeatureScope
    fun provideRewindTimeMs(context: Context): Long {
        return context.resources
            .getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    @Provides
    @FeatureScope
    fun providePlayer(
        context: Context,
        audioAttributes: AudioAttributes,
        trackSelector: TrackSelector,
        rewindTimeMs: Long
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .apply {
                setSeekBackIncrementMs(rewindTimeMs)
                setSeekForwardIncrementMs(rewindTimeMs)
            }
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(trackSelector)
            .build()
    }
}