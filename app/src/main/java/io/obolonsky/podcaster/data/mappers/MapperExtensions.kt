package io.obolonsky.podcaster.data.mappers

import io.obolonsky.podcaster.data.responses.*
import io.obolonsky.podcaster.data.room.entities.*

fun MusicItem.toSong(): Song {
    return Song(
        id = this.id,
        title = this.title,
        mediaUrl = this.mediaUrl,
    )
}

fun List<MusicItem>.toSongList() = this.map { it.toSong() }