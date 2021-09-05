package io.obolonsky.podcaster.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.appComponent
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.di.AppViewModelFactory
import io.obolonsky.podcaster.viewmodels.SongsViewModel
import javax.inject.Inject

class PlayerFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private val songsViewModel: SongsViewModel by viewModels { appViewModelFactory }

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.videoPlayer.player = playerViewModel.player.getPlayer()

        binding.startButton.setOnClickListener {
            if (playerViewModel.player.isPlaying) pausePlay()
            else startPlay()
        }

        binding.rewind.setOnClickListener {
            playerViewModel.player.rewind(15000)
        }

        binding.forward.setOnClickListener {
            playerViewModel.player.forward(15000)
        }

        songsViewModel.songs.observe(viewLifecycleOwner) { musicItems ->
            musicItems?.let { playlist ->
                initMP3File(playlist[0].mediaUrl.toUri()).let { song ->
                    playerViewModel.player.apply {
                        setMediaItem(song)
                        resume()
                    }
                }
            }
        }
        songsViewModel.loadSongs()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerViewModel.player.release()
    }

    private fun initMP3File(uri: Uri): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId("RHCP")
            .build()
    }

    private fun startPlay() {
        playerViewModel.player.resume()
    }

    private fun pausePlay() {
        playerViewModel.player.pause()
    }
}