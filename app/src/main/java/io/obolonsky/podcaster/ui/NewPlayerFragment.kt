package io.obolonsky.podcaster.ui

import android.content.ComponentName
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import coil.load
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.databinding.FragmentPlayerBinding
import io.obolonsky.podcaster.databinding.FragmentPlayerNavigationBinding
import io.obolonsky.podcaster.player.PlayerService
import io.obolonsky.podcaster.player.PlayerService.Companion.GET_PLAYER_COMMAND
import io.obolonsky.podcaster.player.PlayerService.Companion.MUSIC_SERVICE_BINDER_KEY
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class NewPlayerFragment : AbsFragment(R.layout.fragment_player) {

    private val rewindTime by lazy {
        resources.getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    private val binding: FragmentPlayerBinding by viewBinding()
    private val playerNavBinding: FragmentPlayerNavigationBinding by
        viewBinding(viewBindingRootId = R.id.player_navigation)

    private val speedArray by lazy {
        listOf(
            1.0f,
            1.5f,
            2.0f,
            0.8f,
        )
    }

    private val audioManager by lazy {
        getSystemService(requireContext(), AudioManager::class.java) as AudioManager
    }

    private var currentSpeedPosition = 0

    private val List<Float>.next: Float
        get() {
            ++currentSpeedPosition
            if (currentSpeedPosition == speedArray.size) currentSpeedPosition = 0
            return this[currentSpeedPosition]
        }

    private val playerViewModel: PlayerViewModel by viewModels()

    // TODO: move this crap to MusicServiceConnection
    private lateinit var mediaBrowser: MediaBrowserCompat
    private var musicPlayer: MusicPlayer? = null

    private val playerEventListener by lazy {
        object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                val imageResource =
                    if (isPlaying) R.drawable.ic_round_pause
                    else R.drawable.ic_round_play_arrow
                playerNavBinding.exoPlayPause.setImageResource(imageResource)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                playerNavBinding.exoPlaybackSpeed.setSpeedText(
                    getString(
                        R.string.exo_playback_speed,
                        playbackParameters.speed
                    )
                )
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                playerNavBinding.audioTrackTitle.text = mediaMetadata.displayTitle
                playerNavBinding.audioImage.load(mediaMetadata.artworkUri) {
                    crossfade(500)
                }
            }
        }
    }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
        audioManager
        mediaBrowser = MediaBrowserCompat(
            requireContext(),
            ComponentName(requireContext(), PlayerService::class.java),
            ConnectionCallback(),
            /* rootHints */ null
        )
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onStop() {
        super.onStop()
        mediaBrowser.disconnect()
    }

    private fun buildUI() {
        val mediaController = MediaControllerCompat.getMediaController(requireActivity())
        mediaController.sendCommand(
            GET_PLAYER_COMMAND,
            Bundle(),
            ResultReceiver(Handler(Looper.getMainLooper()))
        )

        playerNavBinding.exoPlayPause.setOnClickListener {
            musicPlayer?.apply {
                if (isPlaying) {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        repeat(5) {
                            delay(75L)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 4 - it, 0)
                        }
                        pause()
                    }
                }
                else {
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        resume()
                        repeat(5) {
                            delay(75L)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, it, 0)
                        }
                    }
                }
            }
        }

        playerNavBinding.exoForward.setOnClickListener {
            musicPlayer?.forward(rewindTime)
        }

        playerNavBinding.exoRewind.setOnClickListener {
            musicPlayer?.rewind(rewindTime)
        }

        playerNavBinding.exoPlaybackSpeed.setOnClickListener {
            musicPlayer
                ?.getPlayer()
                ?.playbackParameters = PlaybackParameters(speedArray.next)
        }
    }

    private inner class ResultReceiver(handler: Handler) : android.os.ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val service = resultData.getBinder(MUSIC_SERVICE_BINDER_KEY)
            if (service is PlayerService.MusicServiceBinder) {
                if (binding.exoPlayer.player != null) {
                    return
                }
                val player = service.getExoPlayer()
                player.addListener(playerEventListener)
                musicPlayer = player
                binding.exoPlayer.player = player.getPlayer()

                val mediaController =
                    MediaControllerCompat.getMediaController(requireActivity())
                mediaController.transportControls.prepareFromMediaId("RHCP", null)
            }
        }
    }

    private inner class ConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaBrowser.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    requireActivity(), // Context
                    token
                ).apply {
                    registerCallback(MediaControllerCallback())
                }

                MediaControllerCompat.setMediaController(requireActivity(), mediaController)
            }

            buildUI()
        }
    }

    private inner class MediaControllerCallback: MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {

        }
    }
}