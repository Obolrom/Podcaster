package io.obolonsky.podcaster

import android.content.Context
import android.widget.Toast
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    private val player: SimpleExoPlayer,
    private val context: Context,
): ExoPlayerController {

    private val playbackStateListener = PlaybackStateListener()

    private var lastPosition: Long = 0L

    init {
        player.addListener(playbackStateListener)
    }

    private inner class PlaybackStateListener: Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            val stateString = when (state) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    Toast.makeText(
                        context,
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

    override val currentPosition: Long
        get() = player.currentPosition

    override val isPlaying: Boolean
        get() = player.isPlaying

    override fun getPlayer(): Player {
        return player
    }

    override fun pause() {
        player.let {
            it.pause()
            lastPosition = it.currentPosition
        }
    }

    override fun resume() {
        player.apply {
            seekTo(lastPosition)
            prepare()
            play()
        }
    }

    override fun forward(mills: Long) {
        player.apply {
            seekTo(currentPosition + mills)
        }
    }

    override fun rewind(mills: Long) {
        player.apply {
            seekTo(currentPosition - mills)
        }
    }

    override fun seekTo(position: Long) {
        player.apply {
            seekTo(position)
        }
    }

    override fun setMediaItem(item: MediaItem) {
        player.setMediaItem(item)
    }

    override fun release() {
        player.apply {
            removeListener(playbackStateListener)
            release()
        }
    }
}