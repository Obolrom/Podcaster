package io.obolonsky.podcaster.player

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import io.obolonsky.podcaster.data.repositories.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicDataSource @Inject constructor(
    private val songsRepository: SongsRepository,
) {

    private val _songs = mutableListOf<MediaMetadataCompat>()
    val songs: List<MediaMetadataCompat> get() = _songs

    suspend fun fetch() = withContext(Dispatchers.IO) {
        return@withContext songsRepository.getSongs()
            .map { song ->
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "RHCP")
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, song.id.toString())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, song.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, "no icon")
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, song.mediaUrl)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, "no icon")
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, "no subtitle")
                    .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, "no subtitle")
                    .build()
            }.also { _songs.addAll(it) }
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): MediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach { song ->
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = songs.map { song ->
        val desc = MediaDescriptionCompat.Builder()
            .setMediaUri(song.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
            .setTitle(song.description.title)
            .setSubtitle(song.description.subtitle)
            .setMediaId(song.description.mediaId)
            .setIconUri(song.description.iconUri)
            .build()
        MediaBrowserCompat.MediaItem(desc, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()
}