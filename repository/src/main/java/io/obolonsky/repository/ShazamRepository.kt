package io.obolonsky.repository

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.apihelpers.ShazamSongRecognitionApiHelper
import io.obolonsky.repository.database.daos.ShazamTrackDao
import io.obolonsky.repository.database.entities.ShazamTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ShazamRepository @Inject constructor(
    private val songRecognitionHelper: ShazamSongRecognitionApiHelper,
    private val shazamTrackDao: ShazamTrackDao,
    private val getRelatedTracksHelper: GetRelatedTracksApiHelper,
    private val dispatchers: CoroutineSchedulers,
) : ShazamRepo {

    override suspend fun audioDetect(audioFile: File): Reaction<ShazamDetect, Error> {
        val apiResult = songRecognitionHelper.load(audioFile)

        when (apiResult) {
            is Reaction.Success -> {
                shazamTrackDao.insert(apiResult.data.mapEntity())
            }
            else -> { }
        }

        return apiResult
    }

    override suspend fun getRelatedTracks(url: String): Reaction<List<Track>, Error> {
        return getRelatedTracksHelper.load(url)
    }

    override fun getTracksFlow(): Flow<List<Track>> {
        return shazamTrackDao.getShazamTracksFlow()
            .flowOn(dispatchers.io)
            .map(::mapToTracks)
            .flowOn(dispatchers.computation)
    }

    private fun ShazamDetect.mapEntity(): List<ShazamTrack> {
        return mutableListOf<ShazamTrack>()
            .apply {
                track?.map(tagId)?.also { add(it) }
            }
    }

    private fun mapToTracks(tracks: List<ShazamTrack>) = tracks.map { it.mapToTrack() }

    private fun ShazamTrack.mapToTrack(): Track {
        return Track(
            audioUri = audioUri,
            subtitle = subtitle,
            title = title,
            imageUrls = imageUrls,
            relatedTracksUrl = null,
            relatedTracks = emptyList(),
        )
    }

    private fun Track.map(tagId: String): ShazamTrack? {
        val audioUri = audioUri ?: return null
        val trackTitle = title ?: return null
        val trackSubtitle = subtitle ?: return null
        val imageUrls = imageUrls

        return ShazamTrack(
            id = tagId,
            audioUri = audioUri,
            title = trackTitle,
            subtitle = trackSubtitle,
            imageUrls = imageUrls,
        )
    }
}