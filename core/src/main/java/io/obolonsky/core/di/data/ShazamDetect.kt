package io.obolonsky.core.di.data

import io.obolonsky.core.di.DownloadStatus

data class ShazamDetect(
    val tagId: String,
    val track: Track?,
)

data class Track(
    val audioUri: String,
    val subtitle: String,
    val title: String,
    val imageUrls: List<String>,
    val relatedTracksUrl: String?,
    val relatedTracks: List<Track>,
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED,
) : DownloadableMedia {

    override val mediaUrl = audioUri
}