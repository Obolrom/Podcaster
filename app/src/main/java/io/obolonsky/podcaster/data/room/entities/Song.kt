package io.obolonsky.podcaster.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
    @Ignore var isFavorite: Boolean = false,
) {
    constructor(
        id: Long,
        title: String,
        mediaUrl: String,
    ) : this(
        id = id,
        title = title,
        mediaUrl = mediaUrl,
        isFavorite = false,
    )
}
