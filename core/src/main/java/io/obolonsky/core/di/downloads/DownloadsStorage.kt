package io.obolonsky.core.di.downloads

import androidx.media3.exoplayer.offline.Download
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.Error
import kotlinx.coroutines.flow.SharedFlow

interface DownloadsStorage {

    val downloads: SharedFlow<Reaction<List<Download>>>
}