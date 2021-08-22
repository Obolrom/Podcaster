package io.obolonsky.podcaster.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import io.obolonsky.podcaster.appComponent
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.di.AppViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class PlayerFragment : Fragment() {

    @Inject
    lateinit var appViewModelFactory: AppViewModelFactory

    private val playerViewModel: PlayerViewModel by viewModels { appViewModelFactory }

    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding get() = _binding!!

    private var player: ExoPlayer? = null

    private val playbackStateListener = PlaybackStateListener()

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

        if (player == null) initPlayer()

        val files = context?.assets?.list("media")
        file = Uri.parse("asset:///media/${files?.getOrNull(0)!!}")

        initMP3File(file)
        startPlay()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun initMP3File(uri: Uri) {
        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setMediaId("RHCP")
            .build()

        player?.addMediaItem(mediaItem)
    }

    private fun initPlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(requireContext())
            trackSelector.setParameters(
                trackSelector.buildUponParameters())
            player = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(trackSelector)
                .setLoadControl(DefaultLoadControl())
                .build()
        }

        binding.videoPlayer.player = player
        player?.addListener(playbackStateListener)
    }

    private fun releasePlayer() {
        player?.apply {
            removeListener(playbackStateListener)
            release()
        }
        player = null
    }

    private fun startPlay() {
        player?.apply {
            playWhenReady = playWhenReady
            seekTo(0)
            prepare()
            play()
        }
    }

    private inner class PlaybackStateListener(): Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            val stateString = when (state) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    Toast.makeText(
                        requireContext(),
                        "buffering",
                        Toast.LENGTH_SHORT
                    ).show()
                    "ExoPlayer.STATE_BUFFERING -"
                }
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Timber.d("changed state to $stateString")
        }
    }
}