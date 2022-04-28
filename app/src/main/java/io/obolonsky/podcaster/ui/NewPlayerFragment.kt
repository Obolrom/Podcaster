package io.obolonsky.podcaster.ui

import android.content.ComponentName
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import io.obolonsky.podcaster.MusicPlayer
import io.obolonsky.podcaster.R
import io.obolonsky.podcaster.player.PlayerService
import io.obolonsky.podcaster.player.PlayerService.Companion.GET_PLAYER_COMMAND
import io.obolonsky.podcaster.player.PlayerService.Companion.MUSIC_SERVICE_BINDER_KEY
import io.obolonsky.podcaster.viewmodels.PlayerViewModel
import kotlinx.android.synthetic.main.fragment_player.*
import kotlinx.android.synthetic.main.fragment_player_navigation.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class NewPlayerFragment : AbsFragment(R.layout.fragment_player) {

    private val rewindTime by lazy {
        resources.getInteger(R.integer.player_rewind_time) * TimeUnit.SECONDS.toMillis(1)
    }

    private val speedArray by lazy {
        listOf(
            1.0f,
            1.5f,
            2.0f,
            0.8f,
        )
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
                exo_play_pause?.setImageResource(imageResource)
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                exo_playback_speed?.setSpeedText(
                    getString(
                        R.string.exo_playback_speed,
                        playbackParameters.speed
                    )
                )
            }

            override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                audio_track_title?.text = mediaMetadata.displayTitle
                audio_image?.load(mediaMetadata.artworkUri) {
                    crossfade(500)
                }
            }
        }
    }

    override fun initViewModels() { }

    override fun initViews(savedInstanceState: Bundle?) {
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

        exo_play_pause.setOnClickListener {
            musicPlayer?.apply {
                if (isPlaying) pause()
                else resume()
            }
        }

        exo_forward.setOnClickListener {
            musicPlayer?.forward(rewindTime)
        }

        exo_rewind.setOnClickListener {
            musicPlayer?.rewind(rewindTime)
        }

        exo_playback_speed.setOnClickListener {
            musicPlayer
                ?.getPlayer()
                ?.playbackParameters = PlaybackParameters(speedArray.next)
        }
    }

    private inner class ResultReceiver(handler: Handler) : android.os.ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            val service = resultData.getBinder(MUSIC_SERVICE_BINDER_KEY)
            if (service is PlayerService.MusicServiceBinder) {
                if (exo_player?.player != null) {
                    return
                }
                val player = service.getExoPlayer()
                player.addListener(playerEventListener)
                musicPlayer = player
                exo_player.player = player.getPlayer()

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