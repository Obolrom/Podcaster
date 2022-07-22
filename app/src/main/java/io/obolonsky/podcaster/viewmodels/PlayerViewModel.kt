package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import io.obolonsky.podcaster.data.repositories.SongsRepository
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
) : ViewModel()/*, ExoPlayerController by player*/ {

}