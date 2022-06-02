package io.obolonsky.podcaster.player

import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerListener(
    private val musicService: PlayerService,
) : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_READY) {
            musicService.stopForeground(false)
        }
    }

    override fun onPlayerError(error: PlaybackException) { }
}
