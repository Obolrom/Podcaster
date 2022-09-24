package io.obolonsky.core.di.player

import androidx.media3.datasource.cache.Cache

interface PlayerDependenciesProvider {

    /**
     * [Cache.release] should be called, when no necessary
     */
    val exoCache: Cache

    val dataSourceFactories: PlayerDataSourceFactories
}