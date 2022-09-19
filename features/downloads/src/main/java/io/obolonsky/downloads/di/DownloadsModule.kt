package io.obolonsky.downloads.di

import android.content.Context
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.cronet.CronetDataSource
import androidx.media3.datasource.cronet.CronetUtil
import androidx.media3.exoplayer.offline.DownloadManager
import androidx.media3.exoplayer.scheduler.Requirements
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.downloads.DownloadUtils
import io.obolonsky.downloads.utils.getDownloadDirectory
import kotlinx.coroutines.asExecutor
import java.io.File
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.Executors
import javax.inject.Qualifier

/**
 * Whether the demo application uses Cronet for networking. Note that Cronet does not provide
 * automatic support for cookies (https://github.com/google/ExoPlayer/issues/5975).
 *
 *
 * If set to false, the platform's default network stack is used with a [CookieManager]
 * configured in [DownloadsModule.provideHttpDataSourceFactory].
 */
private const val USE_CRONET_FOR_NETWORKING = true

@Module
internal class DownloadsModule {

    @FeatureScope
    @Provides
    fun provideHttpDataSourceFactory(context: Context): DataSource.Factory {
        val httpDataSourceFactory = if (USE_CRONET_FOR_NETWORKING) {
            val appCtx = context.applicationContext
            val cronetEngine = CronetUtil.buildCronetEngine(appCtx)
            if (cronetEngine != null) {
                CronetDataSource.Factory(
                    cronetEngine,
                    Executors.newSingleThreadExecutor()
                )
            } else null
        } else null

        return httpDataSourceFactory ?: run {
            // We don't want to use Cronet, or we failed to instantiate a CronetEngine.
            val cookieManager = CookieManager()
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)
            CookieHandler.setDefault(cookieManager)
            DefaultHttpDataSource.Factory()
        }
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
            getDownloadDirectory(context),
            DownloadUtils.DOWNLOAD_CONTENT_DIRECTORY
        )
        return SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), databaseProvider)
    }

    @FeatureScope
    @CacheDataSource
    @Provides
    fun provideCacheDataSourceFactory(
        context: Context,
        httpDataSourceFactory: DataSource.Factory,
        downloadCache: Cache,
    ): DataSource.Factory {
        val upstreamFactory = DefaultDataSource.Factory(
            context,
            httpDataSourceFactory
        )
        return androidx.media3.datasource.cache.CacheDataSource.Factory()
            .setCache(downloadCache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheWriteDataSinkFactory(null)
            .setFlags(androidx.media3.datasource.cache.CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    @FeatureScope
    @Provides
    fun provideDownloadManager(
        context: Context,
        databaseProvider: DatabaseProvider,
        downloadCache: Cache,
        dataSourceFactory: DataSource.Factory,
        dispatchers: CoroutineSchedulers,
    ): DownloadManager {
        return DownloadManager(
            context,
            databaseProvider,
            downloadCache,
            dataSourceFactory,
            dispatchers.io.asExecutor()
        ).apply {
            maxParallelDownloads = 2
            requirements = Requirements(Requirements.NETWORK_UNMETERED)
        }
    }
}

@Qualifier
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class CacheDataSource