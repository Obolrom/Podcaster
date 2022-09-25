package io.obolonsky.core.di.common

import androidx.media3.common.MediaItem
import io.obolonsky.core.di.data.Track

object AudioSource {

    private val internalTracks = mutableListOf<Track>()
    val tracks: List<Track> get() = internalTracks

    private val internalMediaItems = mutableListOf<MediaItem>()
    val mediaItems: List<MediaItem> get() = internalMediaItems

    fun addTrack(track: Track) {
        internalTracks.add(track)
    }

    fun addMediaItem(mediaItem: MediaItem) {
        internalMediaItems.add(mediaItem)
    }

    fun clear() {
        internalTracks.clear()
        internalMediaItems.clear()
    }
}