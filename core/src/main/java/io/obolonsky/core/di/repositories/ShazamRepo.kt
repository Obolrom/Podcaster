package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ShazamRepo {

    suspend fun audioDetect(audioFile: File): Reaction<ShazamDetect, Error>

    suspend fun getRelatedTracks(url: String): Reaction<List<Track>, Error>

    fun getTracksFlow(): Flow<List<Track>>
}