package io.obolonsky.podcaster.data.mappers

import io.obolonsky.podcaster.data.responses.*
import io.obolonsky.podcaster.data.room.entities.*

object MusicItemToSongMapper: Mapper<MusicItem, Song> {
    override fun map(input: MusicItem): Song {
        return Song(
            id = input.id,
            title = input.title,
            mediaUrl = input.mediaUrl,
        )
    }
}

fun <I, O> List<I>.mapList(mapper: Mapper<I, O>) = this.map { mapper.map(it) }