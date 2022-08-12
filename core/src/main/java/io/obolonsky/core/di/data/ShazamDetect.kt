package io.obolonsky.core.di.data

data class ShazamDetect(
    val tagId: String,
    val track: Track?,
)

data class Track(
    val audioUri: String?,
    val subtitle: String?,
    val title: String?,
    val imageUrls: List<String>,
)