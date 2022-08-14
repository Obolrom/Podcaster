package io.obolonsky.player_feature.player

import androidx.media3.exoplayer.ExoPlayer
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.player_feature.AudioSource
import javax.inject.Inject

@FeatureScope
class PodcasterPlayer @Inject constructor(
    val player: ExoPlayer,
) {

    init {
        player.playWhenReady = true
//        player.setMediaItems(listOf(
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-californication.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/guano_apes_proud_like_a_god/guano_apes_suzie.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-cant-stop.mp3?raw=true".toUri()),
//            MediaItem.fromUri("https://github.com/Obolrom/MusicLibrary/blob/master/rhcp_californication/red-hot-chili-peppers-dark-necessities.mp3?raw=true".toUri()),
//        ))
        AudioSource.mediaItems.let {
            player.setMediaItems(it)
        }
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