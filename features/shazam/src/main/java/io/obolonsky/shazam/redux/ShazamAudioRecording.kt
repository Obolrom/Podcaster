package io.obolonsky.shazam.redux

import io.obolonsky.core.di.data.ShazamDetect
import io.obolonsky.core.di.data.Track

data class ShazamAudioRecordingState(
    val detected: ShazamDetect? = null,
)

sealed class ShazamAudioRecordingSideEffects {

    data class ShazamDetectedSideEffect(
        val detectedTrack: Track,
    ) : ShazamAudioRecordingSideEffects()
}