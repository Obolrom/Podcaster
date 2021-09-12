package io.obolonsky.podcaster.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import io.obolonsky.podcaster.data.room.interfaces.Identifiable

@Entity(tableName = "songs")
data class Song(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") override val id: Long,
    @ColumnInfo(name = "title") override val title: String,
    @ColumnInfo(name = "media_url") val mediaUrl: String,
): Identifiable
