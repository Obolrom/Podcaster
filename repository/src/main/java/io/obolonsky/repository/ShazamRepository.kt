package io.obolonsky.repository

import dagger.Reusable
import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.reactWith
import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.apihelpers.GetRelatedTracksApiHelper
import io.obolonsky.network.apihelpers.ShazamSongRecognitionApiHelper
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.entities.ShazamTrack
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject

@Reusable
class ShazamRepository @Inject constructor(
    private val songRecognitionHelper: ShazamSongRecognitionApiHelper,
    private val shazamTrackDao: ShazamTrackDao,
    private val getRelatedTracksHelper: GetRelatedTracksApiHelper,
    private val dispatchers: CoroutineSchedulers,
) : ShazamRepo {

    override fun audioDetect(audioFile: File): Flow<Reaction<ShazamDetect>> {
        return flow { emit(songRecognitionHelper.load(audioFile)) }
            .onEach { detectReaction ->
                detectReaction.reactWith(
                    onSuccess = { apiResult ->
                        shazamTrackDao.insert(apiResult.mapEntity())
                    },
                    onError = { }
                )
            }
    }

    override suspend fun saveTrack(track: Track): Reaction<Unit> {
        return try {
            track.map()?.let { shazamTrackDao.insert(it) }

            Reaction.success(Unit)
        } catch (e: Exception) {
            Timber.e(e)
            Reaction.fail(Error.UnknownError(e))
        }
    }

    override suspend fun getRelatedTracks(url: String): Reaction<List<Track>> {
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