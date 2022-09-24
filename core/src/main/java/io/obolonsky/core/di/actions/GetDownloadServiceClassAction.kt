package io.obolonsky.core.di.actions

import androidx.media3.exoplayer.offline.DownloadService

interface GetDownloadServiceClassAction {

    val downloadServiceClass: Class<out DownloadService>
}