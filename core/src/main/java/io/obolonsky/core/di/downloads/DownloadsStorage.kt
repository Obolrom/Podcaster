package io.obolonsky.core.di.downloads

import androidx.media3.exoplayer.offline.Download
import kotlinx.coroutines.flow.SharedFlow

interface DownloadsStorage {

    val downloads: SharedFlow<List<Download>>
}