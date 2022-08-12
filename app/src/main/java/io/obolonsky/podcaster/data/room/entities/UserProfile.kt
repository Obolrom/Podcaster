package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class UserProfile(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val ownMaterialsCount: Int,
    val balance: Int,
    val auditionCount: Int,
    val raiting: Int,
)