package io.obolonsky.shazam_feature.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.shazam_feature.data.usecases.AudioDetectionUseCase
import io.obolonsky.shazam_feature.data.repositories.ShazamRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class ShazamViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val audioDetectionUseCase: AudioDetectionUseCase,
    private val shazamRepository: ShazamRepository,
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
            val shazamDetect = audioDetectionUseCase(audioFile)
            shazamDetect?.let { detected ->
                val relatedTracks = detected.track
                    ?.relatedTracksUrl
                    ?.let { shazamRepository.getRelatedTracks(it) }
                    ?: emptyList()
                val full = detected.track?.copy(relatedTracks = relatedTracks)

                _shazamDetect.emit(detected.copy(track = full))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): ShazamViewModel
    }
}