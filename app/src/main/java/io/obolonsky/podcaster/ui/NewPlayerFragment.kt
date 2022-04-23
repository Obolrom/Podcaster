package io.obolonsky.podcaster.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.data.room.StatefulData
import io.obolonsky.podcaster.viewmodels.MainViewModel
import javax.inject.Inject

@AndroidEntryPoint
class NewPlayerFragment : AbsFragment(R.layout.fragment_player_navigation) {

    private val mainViewModel: MainViewModel by viewModels()

    override fun initViewModels() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) { result ->
            when (result) {
                is StatefulData.Success -> {
//                    allSongsProgressBar.isVisible = false
                    result.data?.let { songs ->
//                        songAdapter.songs = songs
                        mainViewModel.playOrToggleSong(songs[0])
                    }
                }
                is StatefulData.Error -> Unit
                is StatefulData.Loading -> {
//                    allSongsProgressBar.isVisible = true
                }
            }
        }
    }

    override fun initViews(savedInstanceState: Bundle?) { }
}