package io.obolonsky.repository.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shazam_tracks")
data class ShazamTrack(
    @PrimaryKey
    @ColumnInfo(name = "tag_id") override val id: Guid,
    @ColumnInfo(name = "audio_uri") val audioUri: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "subtitle") val subtitle: String,
    @ColumnInfo(name = "image_urls") val imageUrls: List<String>,
) : Identifiable