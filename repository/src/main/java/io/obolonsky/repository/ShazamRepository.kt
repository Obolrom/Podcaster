package io.obolonsky.repository

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.apihelpers.ShazamSongRecognitionApiHelper
import java.io.File
import javax.inject.Inject

class ShazamRepository @Inject constructor(
    private val songRecognitionHelper: ShazamSongRecognitionApiHelper,
    private val getRelatedTracksHelper: GetRelatedTracksApiHelper,
) : ShazamRepo {

    override suspend fun audioDetect(audioFile: File): Reaction<ShazamDetect, Error> {
        return songRecognitionHelper.load(audioFile)
    }

    override suspend fun getRelatedTracks(url: String): Reaction<List<Track>, Error> {
        return getRelatedTracksHelper.load(url)
    }
}