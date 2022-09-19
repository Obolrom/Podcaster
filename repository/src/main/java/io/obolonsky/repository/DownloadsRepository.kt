package io.obolonsky.repository

import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.repositories.DownloadsRepo
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.storage.database.ExoPlayerDao
import io.obolonsky.storage.database.daos.ShazamTrackDao
import io.obolonsky.storage.database.entities.ShazamTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class DownloadsRepository @Inject constructor(
    private val shazamTrackDao: ShazamTrackDao,
    private val exoDao: ExoPlayerDao,
    private val dispatchers: CoroutineSchedulers,
) : DownloadsRepo {

    init {
        CoroutineScope(SupervisorJob()).launch {
            exoDao.getDownloadsFlow()
                .onEach { Timber.d("exoDownloads $it") }
                .collect()
        }
    }

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