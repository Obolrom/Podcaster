package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class BookAuthor(
    val id: String,
    val firstName: String,
    val lastName: String,
    val raiting: Int,
)