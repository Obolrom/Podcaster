package io.obolonsky.core.di

import androidx.media3.exoplayer.offline.Download

enum class DownloadStatus {
    QUEUED,
    STOPPED,
    DOWNLOADING,
    COMPLETED,
    FAILED,
    REMOVING,
    RESTARTING,
    NOT_DOWNLOADED,
}

fun Download?.mapStatus() = when (this?.state) {
    Download.STATE_QUEUED -> DownloadStatus.QUEUED
    Download.STATE_STOPPED -> DownloadStatus.STOPPED
    Download.STATE_DOWNLOADING -> DownloadStatus.DOWNLOADING
    Download.STATE_COMPLETED -> DownloadStatus.COMPLETED
    Download.STATE_FAILED -> DownloadStatus.FAILED
    Download.STATE_REMOVING -> DownloadStatus.REMOVING
    Download.STATE_RESTARTING -> DownloadStatus.RESTARTING
    else -> DownloadStatus.NOT_DOWNLOADED
}