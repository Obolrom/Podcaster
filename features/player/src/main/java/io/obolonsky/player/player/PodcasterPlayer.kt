package io.obolonsky.player.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import io.obolonsky.core.di.common.AudioSource
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class PodcasterPlayer @Inject constructor(
    val player: ExoPlayer,
) {

    init {
        player.playWhenReady = false
//        player.setMediaItems(listOf(
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/guano_apes_proud_like_a_god/guano_apes_suzie.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-cant-stop.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-dark-necessities.mp3?raw=true".toUri()),
//        ))
        AudioSource.mediaItems.let {
            player.setMediaItems(it)
        }
//        player.setMediaItem(
//            MediaItem.fromUri("https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview122/v4/73/e3/1f/73e31fb0-bfe0-611a-9f27-90722d0719d5/mzaf_17776609222718316417.plus.aac.ep.m4a")
//        )
    }

    fun play() {
        player.prepare()
    }

    fun forward() {
        player.seekForward()
    }

    fun rewind() {
        player.seekBack()
    }

    fun pause() {
        player.pause()
    }

    fun release() {
        player.release()
    }
}