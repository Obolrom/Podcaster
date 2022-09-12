package io.obolonsky.repository

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.apihelpers.ShazamSongRecognitionApiHelper
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.entities.ShazamTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern
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

    override suspend fun deleteRecentTrackTrack(track: Track) {
        withContext(dispatchers.computation) {
            track.audioUri
                ?.let(::getGuid)
                ?.let { trackId ->
                    withContext(dispatchers.io) {
                        shazamTrackDao.delete(trackId)
                    }
                }
        }
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
                track?.map()?.also { add(it) }
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

    private fun Track.map(): ShazamTrack? {
        val audioUri = audioUri ?: return null
        val id = getGuid(audioUri)
        val trackTitle = title ?: return null
        val trackSubtitle = subtitle ?: return null
        val imageUrls = imageUrls

        return ShazamTrack(
            id = id.toString(),
            audioUri = audioUri,
            title = trackTitle,
            subtitle = trackSubtitle,
            imageUrls = imageUrls,
        )
    }

    private fun getGuid(string: String): String? {
        val regex = "\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12}"
        val pairRegex: Pattern = Pattern.compile(regex)
        val matcher: Matcher = pairRegex.matcher(string)
        while (matcher.find()) {
            return matcher.group(0) ?: null
        }
        return null
    }
}