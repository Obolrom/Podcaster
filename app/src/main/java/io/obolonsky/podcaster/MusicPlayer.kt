package io.obolonsky.podcaster

import android.content.Context
import android.widget.Toast
import com.google.android.exoplayer2.*
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicPlayer @Inject constructor(
    private val exoPlayer: SimpleExoPlayer,
    @ApplicationContext private val context: Context,
): ExoPlayerController {

    private val playbackStateListener = PlaybackStateListener()

    private var lastPosition: Long = 0L

    init {
        exoPlayer.addListener(playbackStateListener)
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
        get() = exoPlayer.currentPosition

    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    override fun getPlayer(): Player {
        return exoPlayer
    }

    override fun pause() {
        exoPlayer.let {
            it.pause()
            lastPosition = it.currentPosition
        }
    }

    override fun resume() {
        exoPlayer.apply {
            seekTo(lastPosition)
            prepare()
            play()
        }
    }

    override fun forward(mills: Long) {
        exoPlayer.apply {
            seekTo(currentPosition + mills)
        }
    }

    override fun rewind(mills: Long) {
        exoPlayer.apply {
            seekTo(currentPosition - mills)
        }
    }

    override fun seekTo(position: Long) {
        exoPlayer.apply {
            seekTo(position)
        }
    }

    override fun setMediaItem(item: MediaItem) {
        exoPlayer.setMediaItem(item)
    }

    override fun release() {
        exoPlayer.apply {
            removeListener(playbackStateListener)
            release()
        }
    }
}