package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.data.Track
import kotlinx.coroutines.flow.Flow

interface DownloadsRepo {

    fun getTracksFlow(): Flow<List<Track>>
}