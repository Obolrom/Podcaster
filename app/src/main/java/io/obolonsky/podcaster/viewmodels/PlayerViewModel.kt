package io.obolonsky.podcaster.viewmodels

import androidx.lifecycle.ViewModel
import io.obolonsky.podcaster.ExoPlayerController
import io.obolonsky.podcaster.MusicPlayer
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    val player: MusicPlayer,
) : ViewModel(), ExoPlayerController by player {


}