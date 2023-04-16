package io.obolonsky.shazam.redux

import io.obolonsky.core.di.data.ShazamDetect

data class ShazamAudioRecordingState(
    val detected: ShazamDetect? = null,
)