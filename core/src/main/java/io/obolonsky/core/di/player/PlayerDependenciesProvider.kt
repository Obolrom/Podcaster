package io.obolonsky.core.di.player

import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.RenderersFactory

interface PlayerDependenciesProvider {

    /**
     * [Cache.release] should be called, when no necessary
     */
    val exoCache: Cache

    val renderersFactory: RenderersFactory

    val dataSourceFactories: PlayerDataSourceFactories
}