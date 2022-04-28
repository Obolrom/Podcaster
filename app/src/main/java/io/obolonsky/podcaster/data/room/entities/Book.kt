package io.obolonsky.podcaster.data.room.entities

import androidx.room.Entity

@Entity
data class Book(
    val id: String,
    val bookTitle: String,
    val imageUrl: String,
    val category: String,
    val duration: Long,
    val bookAuthor: BookAuthor,
    val voiceOverAuthor: VoiceOverAuthor,
    val raiting: Int,
    val description: String,
    val auditionsCount: Int,
    val lastChapter: Chapter?,
    val chapters: List<Chapter>
)
