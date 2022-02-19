package io.obolonsky.podcaster.di.modules

import android.content.Context
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object PlayerModule {

    @Singleton
    @Provides
    fun provideDefaultTrackSelector(
        context: Context
    ): DefaultTrackSelector {
        val trackSelector = DefaultTrackSelector(context)

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
        context: Context,
        trackSelector: DefaultTrackSelector,
        loadControl: DefaultLoadControl,
    ): SimpleExoPlayer {
        return SimpleExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(loadControl)
            .build()
    }

}