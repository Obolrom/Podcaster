package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class VoiceOverAuthor(
    override val id: String,
    val firstName: String,
    val lastName: String,
    val userName: String,
    val email: String,
    val raiting: Int,
) : Identifiable