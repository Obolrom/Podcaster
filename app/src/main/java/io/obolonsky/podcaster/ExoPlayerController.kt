package io.obolonsky.podcaster

import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player

interface ExoPlayerController {

    val currentPosition: Long

    val isPlaying: Boolean

    fun getPlayer(): Player

    fun stop()

    fun pause()

    fun resume()

    fun forward(mills: Long)

    fun rewind(mills: Long)

    fun seekTo(position: Long)

    fun setMediaItem(item: MediaItem)

    fun freeUpResourcesAndRelease()

    fun addListener(listener: Player.Listener)
}