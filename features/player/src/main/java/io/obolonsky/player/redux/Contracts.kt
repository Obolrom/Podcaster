package io.obolonsky.player.redux

import io.obolonsky.core.di.data.Track

data class PlayerUiState(
    val tracks: List<Track>?,
)