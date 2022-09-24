package io.obolonsky.core.di.player

import androidx.media3.datasource.DataSource

data class PlayerDataSourceFactories(
    val httpDataSourceFactory: DataSource.Factory,
    val cacheDataSourceFactory: DataSource.Factory,
)