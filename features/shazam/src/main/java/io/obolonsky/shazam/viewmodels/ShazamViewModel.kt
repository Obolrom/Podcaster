@file:OptIn(ExperimentalCoroutinesApi::class)

package io.obolonsky.shazam.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.reactWithSuccessOrDefault
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.shazam.data.usecases.AudioDetectionUseCase
import io.obolonsky.shazam.di.ScopedShazamRepo
import io.obolonsky.shazam.recorder.ShazamMediaRecorder
import io.obolonsky.shazam.redux.ShazamAudioRecordingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.File

class ShazamViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted("output") private val outputFilepath: String,
    @Assisted("outputDir") private val outputDir: String,
    @Assisted recordDurationMs: Long,
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

    private val audioRecorder by lazy {
        ShazamMediaRecorder(
            outputFile = File(outputFilepath),
            recordDurationMs = recordDurationMs,
        )
    }

    override val container: Container<ShazamAudioRecordingState, Unit> = container(
        initialState = ShazamAudioRecordingState(
            detected = null,
        ),
    )

    fun record() = intent {
        flow { emit(audioRecorder.record()) }
            .map { File(outputFilepath) }
           /* .onEach {
                    val full = Track(
                        audioUri = File(outputDir, RECORDED_AUDIO_FILENAME).absolutePath,
                        subtitle = "recorded",
                        title = "recorded",
                        imageUrls = emptyList(),
                        relatedTracks = emptyList(),
                        relatedTracksUrl = null,
                    )
                reduce {
                    state.copy(detected = ShazamDetect("", full))
                }
                _shazamDetect.emit(ShazamDetect("", full))
            }*/
            .flatMapLatest(audioDetectionUseCase::invoke)
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
        return shazamRepository.getRelatedTracks(url)
            .reactWithSuccessOrDefault { emptyList() }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            savedStateHandle: SavedStateHandle,
            @Assisted("output") outputFilepath: String,
            @Assisted("outputDir") outputDir: String,
            recordDurationMs: Long,
        ): ShazamViewModel
    }

    private companion object {
        const val RECORDED_AUDIO_FILENAME = "fileToDetect.mp3"
    }
}