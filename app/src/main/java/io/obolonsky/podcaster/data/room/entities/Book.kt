package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class Book(
    override val id: String,
    val title: String,
    val imageUrl: String,
    val category: String?,
    val duration: Long?,
    val bookAuthor: BookAuthor?,
    val voiceOverAuthor: VoiceOverAuthor?,
    val raiting: Int,
    val description: String,
    val auditionsCount: Int?,
    val lastChapter: Chapter?,
    val chapters: List<Chapter> = mutableListOf(),
) : Identifiable
