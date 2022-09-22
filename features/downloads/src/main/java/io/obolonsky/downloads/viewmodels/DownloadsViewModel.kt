package io.obolonsky.downloads.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.downloads.DownloadsStorage
import io.obolonsky.core.di.mapStatus
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.downloads.usecases.GetTracksFlowUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

class DownloadsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getTracksFlowUseCase: GetTracksFlowUseCase,
    private val dispatchers: CoroutineSchedulers,
    private val downloadStorage: DownloadsStorage,
) : ViewModel(), DownloadsStorage by downloadStorage {

    private val _tracks by lazy {
        MutableSharedFlow<List<Track>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val tracks: SharedFlow<List<Track>> get() = _tracks.asSharedFlow()

    fun load() {
        getTracksFlowUseCase()
            .combine(downloads) { tracks, downloads ->
                val downloadsData = when (downloads) {
                    is Reaction.Success -> downloads.data

                    is Reaction.Fail -> null
                }
                val tracksWithDownloadStatuses = tracks
                    .map { track ->
                        val download = downloadsData?.firstOrNull {
                            it.request.id == track.audioUri
                        }
                        track.copy(downloadStatus = download.mapStatus())
                    }
                _tracks.emit(tracksWithDownloadStatuses)
            }
            .flowOn(dispatchers.computation)
            .launchIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): DownloadsViewModel
    }
}