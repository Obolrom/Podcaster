package io.obolonsky.storage.downloads

import androidx.media3.exoplayer.offline.Download
import io.obolonsky.core.di.downloads.DownloadsStorage
import io.obolonsky.core.di.scopes.ApplicationScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@ApplicationScope
internal class DownloadsStorageImpl @Inject constructor() : DownloadsStorage {

    val mutableDownloads by lazy {
        MutableSharedFlow<List<Download>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    override val downloads: SharedFlow<List<Download>> get() = mutableDownloads.asSharedFlow()
}