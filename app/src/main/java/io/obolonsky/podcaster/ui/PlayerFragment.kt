package io.obolonsky.podcaster.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import io.obolonsky.podcaster.ExoPlayerController
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.appComponent
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.di.AppViewModelFactory
import io.obolonsky.podcaster.misc.getMegaBytes
import javax.inject.Inject

class PlayerFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    private val player: ExoPlayerController by lazy { MusicPlayer(requireContext()) }

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private lateinit var file: Uri

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

        binding.videoPlayer.player = player.getPlayer()

        val files = context?.assets?.list("media")
        file = "asset:///media/${files?.firstOrNull()}".toUri()
        val fuckIt = context?.assets?.open("media/${files?.firstOrNull()}")
        Toast.makeText(
            requireContext(),
            fuckIt?.getMegaBytes(),
            Toast.LENGTH_SHORT
        ).show()

        binding.startButton.setOnClickListener {
            if (player.isPlaying) pausePlay()
            else startPlay()
        }

        binding.rewind.setOnClickListener {
            player.apply { seekTo(currentPosition - 15000) }
        }

        binding.forward.setOnClickListener {
            player.apply { seekTo(currentPosition + 15000) }
        }

        player.addMediaItem(initMP3File(file))
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }

    private fun initMP3File(uri: Uri): MediaItem {
        return MediaItem.Builder()
            .setUri(uri)
            .setMediaId("RHCP")
            .build()
    }

    private fun startPlay() {
        player.resume()
    }

    private fun pausePlay() {
        player.pause()
    }
}