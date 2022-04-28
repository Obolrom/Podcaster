package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class BookAuthor(
    override val id: String,
    val firstName: String,
    val lastName: String,
    val raiting: Int,
) : Identifiable