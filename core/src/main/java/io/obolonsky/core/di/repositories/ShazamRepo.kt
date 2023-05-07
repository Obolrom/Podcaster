package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ShazamRepo {

    /**
     * Returns SINGLE value
     */
    fun audioDetect(audioFile: File): Flow<Reaction<ShazamDetect>>

    suspend fun saveTrack(track: Track): Reaction<Unit>

    suspend fun getRelatedTracks(url: String): Reaction<List<Track>>

    suspend fun deleteRecentTrackTrack(track: Track)

    fun getTracksFlow(): Flow<List<Track>>
}