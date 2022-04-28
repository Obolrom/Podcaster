package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class Chapter(
    val id: String,
    val bookId: String,
    val title: String,
    val imageUrl: String,
    val mediaUrl: String,
    val duration: Long,
    val lastTimeStamp: Long?,
)