package io.obolonsky.podcaster

import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import dagger.hilt.android.scopes.ServiceScoped
import timber.log.Timber
import javax.inject.Inject

@ServiceScoped
class MusicPlayer @Inject constructor(
    private val exoPlayer: SimpleExoPlayer,
) : ExoPlayerController {

    private val playerListeners = mutableListOf<Player.Listener>()

    private var lastPosition: Long = 0L

    init {
        exoPlayer.addListener(PlaybackStateListener())
    }

    private inner class PlaybackStateListener : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            super.onPlaybackStateChanged(state)
            val stateString = when (state) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> { "ExoPlayer.STATE_BUFFERING -" }
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Timber.d("changed state to $stateString")
        }
    }

    val currentWindow: Int
        get() = exoPlayer.currentWindowIndex

    override val currentPosition: Long
        get() = exoPlayer.currentPosition

    override val isPlaying: Boolean
        get() = exoPlayer.isPlaying

    override fun getPlayer(): Player {
        return exoPlayer
    }

    override fun addListener(listener: Player.Listener) {
        exoPlayer.addListener(listener)
    }

    override fun stop() {
        exoPlayer.stop()
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

    fun addMediaSource(mediaSource: MediaSource) {
        exoPlayer.addMediaSource(mediaSource)
    }

    override fun freeUpResourcesAndRelease() {
        exoPlayer.apply {
            playerListeners.forEach(::removeListener)
            release()
        }
    }
}