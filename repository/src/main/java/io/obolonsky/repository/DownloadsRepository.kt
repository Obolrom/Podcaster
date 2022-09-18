package io.obolonsky.repository

import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.DownloadsRepo
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.entities.ShazamTrack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DownloadsRepository @Inject constructor(
    private val shazamTrackDao: ShazamTrackDao,
    private val dispatchers: CoroutineSchedulers,
) : DownloadsRepo {

    override fun getTracksFlow(): Flow<List<Track>> {
        return shazamTrackDao.getShazamTracksFlow()
            .flowOn(dispatchers.io)
            .map(::mapToTracks)
            .flowOn(dispatchers.computation)
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
}