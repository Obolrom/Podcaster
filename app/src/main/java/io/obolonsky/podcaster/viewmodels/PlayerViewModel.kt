package io.obolonsky.podcaster.viewmodels

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.ViewModel
import io.obolonsky.podcaster.data.repositories.SongsRepository
import io.obolonsky.podcaster.data.room.entities.Song
import io.obolonsky.podcaster.player.MusicServiceConnection
import io.obolonsky.podcaster.player.isPlayEnabled
import io.obolonsky.podcaster.player.isPlaying
import io.obolonsky.podcaster.player.isPrepared
import javax.inject.Inject

class PlayerViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val songsRepository: SongsRepository,
) : ViewModel()/*, ExoPlayerController by player*/ {

    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState

    private val subscriptionCallback by lazy {
        object : MediaBrowserCompat.SubscriptionCallback() {
            override fun onChildrenLoaded(
                parentId: String,
                children: MutableList<MediaBrowserCompat.MediaItem>
            ) {
                super.onChildrenLoaded(parentId, children)
            }
        }
    }

    fun playOrToggleSong(mediaItem: Song, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaItem.id.toString() ==
            curPlayingSong.value?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaId, null)
        }
    }

    init {
//        musicServiceConnection.subscribe(
//            parentId = Constants.MEDIA_ROOT_ID,
//            callback = subscriptionCallback,
//        )
    }

    override fun onCleared() {
        super.onCleared()
//        musicServiceConnection.unsubscribe(
//            parentId = Constants.MEDIA_ROOT_ID,
//            subscriptionCallback
//        )
    }
}