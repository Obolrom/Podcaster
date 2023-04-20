@file:OptIn(ExperimentalCoroutinesApi::class)

package io.obolonsky.shazam.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track
import io.obolonsky.core.di.reactWithSuccessOrDefault
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.shazam.data.usecases.AudioDetectionUseCase
import io.obolonsky.shazam.di.ScopedShazamRepo
import io.obolonsky.shazam.recorder.ShazamMediaRecorder
import io.obolonsky.shazam.redux.ShazamAudioRecordingSideEffects
import io.obolonsky.shazam.redux.ShazamAudioRecordingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
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
) : ViewModel(), ContainerHost<ShazamAudioRecordingState, ShazamAudioRecordingSideEffects> {

    private val audioRecorder by lazy {
        ShazamMediaRecorder(
            outputFile = File(outputFilepath),
            recordDurationMs = recordDurationMs,
        )
    }

    override val container: Container<ShazamAudioRecordingState, ShazamAudioRecordingSideEffects> = container(
        initialState = ShazamAudioRecordingState(
            detected = null,
            isRecordingInProgress = false,
        ),
    )

    fun record() = intent {
        if (state.isRecordingInProgress) {
            return@intent
        }

        flow { emit(audioRecorder.record()) }
            .map { File(outputFilepath) }
            .onEach { reduce { state.copy(isRecordingInProgress = false) } }
//            .flatMapLatest(audioDetectionUseCase::invoke)
            .map { Reaction.success(ShazamDetect(tagId = "", track = Track(audioUri = outputFilepath, subtitle = "recorded", title = "recorded", imageUrls = emptyList(), relatedTracks = emptyList(), relatedTracksUrl = null))) }
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
                    if (full != null) {
                        postSideEffect(
                            ShazamAudioRecordingSideEffects.ShazamDetectedSideEffect(
                                detectedTrack = full
                            )
                        )
                    }
                },
                onError = { error ->
                    Timber.d(error.toString())
                }
            )
            .onStart {
                reduce { state.copy(isRecordingInProgress = true) }
            }
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
}