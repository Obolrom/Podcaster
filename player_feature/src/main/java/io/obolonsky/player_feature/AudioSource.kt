package io.obolonsky.player_feature

import androidx.media3.common.MediaItem

object AudioSource {

    private val internalMediaItems = mutableListOf<MediaItem>()
    val mediaItems: List<MediaItem> get() = internalMediaItems

    fun addMediaItem(mediaItem: MediaItem) {
        internalMediaItems.add(mediaItem)
    }

    fun clear() {
        internalMediaItems.clear()
    }
}