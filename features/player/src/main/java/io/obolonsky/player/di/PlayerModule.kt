package io.obolonsky.player.di

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.TrackSelector
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.player.R
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

@Module
class PlayerModule {

    @FeatureScope
    @Provides
    fun provideTrackSelector(
        context: Context,
    ): TrackSelector {
        return DefaultTrackSelector(context)
    }

    @FeatureScope
    @Provides
    fun provideAudioAttributes(): AudioAttributes {
        return AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()
    }

    @FeatureScope
    @Provides
    fun provideDatabaseProvider(context: Context): DatabaseProvider {
        return StandaloneDatabaseProvider(context.applicationContext)
    }

    @FeatureScope
    @Provides
    fun provideDownloadCache(
        context: Context,
        databaseProvider: DatabaseProvider,
    ): Cache {
        val downloadContentDirectory = File(
            context.getExternalFilesDir(null) ?: context.filesDir,
            "downloads"
        )
        return SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), databaseProvider)
    }

    @FeatureScope
    @Provides
    fun provideCacheDataSourceFactory(
        context: Context,
        downloadCache: Cache,
    ): DataSource.Factory {
        val httpDataSourceFactory = run {
            // We don't want to use Cronet, or we failed to instantiate a CronetEngine.
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
            CookieHandler.setDefault(cookieManager)
            DefaultHttpDataSource.Factory()
        }
        val upstreamFactory = DefaultDataSource.Factory(
            context,
            httpDataSourceFactory
        )
        return CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    @FeatureScope
    @Provides
    fun provideRewindTimeMs(context: Context): Long {
        return context.resources
            .getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    @FeatureScope
    @Provides
    fun providePlayer(
        context: Context,
        audioAttributes: AudioAttributes,
        cacheDataSourceFactory: DataSource.Factory,
        trackSelector: TrackSelector,
        rewindTimeMs: Long
    ): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .apply {
                setSeekBackIncrementMs(rewindTimeMs)
                setSeekForwardIncrementMs(rewindTimeMs)
            }
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .setHandleAudioBecomingNoisy(true)
            .setTrackSelector(trackSelector)
            .build()
    }
}