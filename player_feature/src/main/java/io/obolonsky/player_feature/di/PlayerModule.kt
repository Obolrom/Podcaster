package io.obolonsky.player_feature.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides

@Module
class PlayerModule {

    @Provides
    @FeatureScope
    fun providePlayer(
        context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .build()
    }

    @Provides
    fun provideMediaSession(
        context: Context,
        exoPlayer: ExoPlayer,
    ): MediaSession {
        return MediaSession.Builder(context, exoPlayer)
            .build()
    }

}