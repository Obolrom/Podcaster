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

    init {
        intent {
            postSideEffect(
                ShazamAudioRecordingSideEffects.ShazamDetectedSideEffect(
                    detectedTrack = Track(
                        audioUri = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/44/06/4a/44064ab5-d4c0-5f3f-03f2-22a8e5ac19cf/mzaf_3474282449070831997.plus.aac.ep.m4a",
                        subtitle = "Donna Summer",
                        title = "Hot Stuff",
                        imageUrls = listOf(
                            "https://is1-ssl.mzstatic.com/image/thumb/Features125/v4/0d/73/d8/0d73d872-4ab8-b500-8197-57125c1c1c7c/mzl.cmfjmvmc.jpg/800x800cc.jpg",
                            "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/1200x1200cc.jpg",
                            "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/400x400cc.jpg",
                        ),
                        relatedTracks = listOf(
                            Track(
                                audioUri = "fuck", // "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview122/v4/d7/f4/f2/d7f4f22d-0d13-bdd3-5234-da4915e0f989/mzaf_8359147047626143877.plus.aac.ep.m4a",
                                subtitle = "Some other author",
                                title = "Some other track",
                                imageUrls = listOf(
                                    "https://is1-ssl.mzstatic.com/image/thumb/Features125/v4/0d/73/d8/0d73d872-4ab8-b500-8197-57125c1c1c7c/mzl.cmfjmvmc.jpg/800x800cc.jpg",
                                    "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/400x400cc.jpg",
                                    "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/400x400cc.jpg",
                                ),
                                relatedTracks = emptyList(),
                                relatedTracksUrl = "https://cdn.shazam.com/shazam/v3/en/GB/iphone/-/tracks/track-similarities-id-303871?startFrom=0&pageSize=20&connected=",
                            ),
                            Track(
                                audioUri = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/95/06/91/95069168-02c8-0082-0a35-4b91bdcea188/mzaf_954001547690755030.plus.aac.ep.m4a",
                                subtitle = "Third author",
                                title = "Third song",
                                imageUrls = listOf(
                                    "https://is1-ssl.mzstatic.com/image/thumb/Features125/v4/0d/73/d8/0d73d872-4ab8-b500-8197-57125c1c1c7c/mzl.cmfjmvmc.jpg/800x800cc.jpg",
                                    "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/400x400cc.jpg",
                                    "https://is5-ssl.mzstatic.com/image/thumb/Music116/v4/66/a7/b0/66a7b03a-c02c-73c2-c7f2-f2afdf05a8dc/06UMGIM04498.rgb.jpg/400x400cc.jpg",
                                ),
                                relatedTracks = emptyList(),
                                relatedTracksUrl = "https://cdn.shazam.com/shazam/v3/en/GB/iphone/-/tracks/track-similarities-id-303871?startFrom=0&pageSize=20&connected=",
                            ),
                        ),
                        relatedTracksUrl = "https://cdn.shazam.com/shazam/v3/en/GB/iphone/-/tracks/track-similarities-id-303871?startFrom=0&pageSize=20&connected=",
                    )
                )
            )
        }
    }

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