package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import java.io.File

interface ShazamRepo {

    suspend fun audioDetect(audioFile: File): ShazamDetect?

    suspend fun getRelatedTracks(url: String): List<Track>
}