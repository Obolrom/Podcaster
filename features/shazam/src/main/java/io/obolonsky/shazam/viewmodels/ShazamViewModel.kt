package io.obolonsky.shazam.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.shazam.data.usecases.AudioDetectionUseCase
import io.obolonsky.shazam.di.ScopedShazamRepo
import io.obolonsky.shazam.redux.ShazamAudioRecordingState
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.File

class ShazamViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val audioDetectionUseCase: AudioDetectionUseCase,
    private val shazamRepository: ScopedShazamRepo,
) : ViewModel(), ContainerHost<ShazamAudioRecordingState, Unit> {

    private val _shazamDetect by lazy {
        MutableSharedFlow<ShazamDetect>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }
    val shazamDetect: SharedFlow<ShazamDetect> get() = _shazamDetect.asSharedFlow()

    override val container: Container<ShazamAudioRecordingState, Unit> = container(
        initialState = ShazamAudioRecordingState(
            detected = null,
        ),
    )

    fun audioDetect(audioFile: File) = intent {
        audioDetectionUseCase(audioFile)
            .reactWith(
                onSuccess = { detected ->
                    val relatedTracks = detected.track
                        ?.relatedTracksUrl
                        ?.let { getRelatedTracks(it) }
                        .orEmpty()
                    val full = detected.track?.copy(relatedTracks = relatedTracks)

                    reduce {
                        state.copy(detected = detected.copy(track = full))
                    }
                    _shazamDetect.emit(detected.copy(track = full))
                },
                onError = { error ->
                    Timber.d(error.toString())
                }
            )
            .collect()
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