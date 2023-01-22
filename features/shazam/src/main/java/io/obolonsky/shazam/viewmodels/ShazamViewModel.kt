package io.obolonsky.shazam.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.reactWith
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.shazam.data.usecases.AudioDetectionUseCase
import io.obolonsky.shazam.data.usecases.DeleteRecentTrackUseCase
import io.obolonsky.shazam.di.ScopedShazamRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class ShazamViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val audioDetectionUseCase: AudioDetectionUseCase,
    private val deleteRecentTrackUseCase: DeleteRecentTrackUseCase,
    private val shazamRepository: ScopedShazamRepo,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    private val _shazamDetect by lazy {
        MutableSharedFlow<ShazamDetect>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val shazamDetect: SharedFlow<ShazamDetect> get() = _shazamDetect.asSharedFlow()

    fun audioDetect(audioFile: File) {
        viewModelScope.launch(dispatchers.computation) {
            audioDetectionUseCase(audioFile)
                .reactWith(
                    onSuccess = { detected ->
                        delay(TimeUnit.SECONDS.toMillis(1))
                        val relatedTracks = detected.track
                            ?.relatedTracksUrl
                            ?.let { getRelatedTracks(it) }
                            ?: emptyList()
                        val full = detected.track?.copy(relatedTracks = relatedTracks)

                        _shazamDetect.emit(detected.copy(track = full))
                    },
                    onError = { error ->
                        Timber.d(error.toString())
                    }
                )
        }
    }

    fun deleteRecentTrack(track: Track) {
        viewModelScope.launch {
            deleteRecentTrackUseCase(track)
        }
    }

    fun getRecentShazamTracks(): Flow<List<Track>> {
        return shazamRepository.getTracksFlow()
    }

    private suspend fun getRelatedTracks(url: String): List<Track> {
        return when (val response = shazamRepository.getRelatedTracks(url)) {
            is Reaction.Success -> {
                response.data
            }

            is Reaction.Fail -> {
                emptyList()
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): ShazamViewModel
    }
}