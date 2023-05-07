package io.obolonsky.player.redux

import io.obolonsky.core.di.data.Track

data class PlayerUiState(
    val currentPlaying: Track? = null,
    val tracks: List<Track>? = null,
)