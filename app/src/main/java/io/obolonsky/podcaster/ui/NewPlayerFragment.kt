package io.obolonsky.podcaster.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.viewmodels.MainViewModel
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player_navigation.*
import javax.inject.Inject

@AndroidEntryPoint
class NewPlayerFragment : AbsFragment(R.layout.fragment_player_navigation) {

    private val mainViewModel: MainViewModel by viewModels()

    private val songsViewModel: SongsViewModel by viewModels()

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun initViewModels() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is StatefulData.Success -> {
//                    allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
//                        songAdapter.songs = songs
//                        mainViewModel.playOrToggleSong(songs[0])
                        playerViewModel.playOrToggleSong(songs[0])
                    }
                }
                is StatefulData.Error -> Unit
                is StatefulData.Loading -> {
//                    allSongsProgressBar.isVisible = true
                }
            }
        }
    }

    override fun initViews(savedInstanceState: Bundle?) {
//        exo_player.player?.addListener(object : Player.Listener {
//            override fun onIsPlayingChanged(isPlaying: Boolean) {
//                val imageResource =
//                    if (isPlaying) R.drawable.ic_round_pause
//                    else R.drawable.ic_round_play_arrow
//                exo_play_pause.setImageResource(imageResource)
//            }
//        })

        exo_play_pause.setOnClickListener {
            playerViewModel.playOrToggleSong(mainViewModel.mediaItems.value?.data!![0], true)
        }
    }
}