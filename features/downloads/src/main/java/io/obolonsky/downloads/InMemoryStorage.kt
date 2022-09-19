package io.obolonsky.downloads

import androidx.media3.exoplayer.offline.Download
import io.obolonsky.core.di.scopes.FeatureScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@FeatureScope
class InMemoryStorage @Inject constructor() {

    internal val mutableDownloads by lazy {
        MutableSharedFlow<List<Download>>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    }
    val downloads: SharedFlow<List<Download>> get() = mutableDownloads.asSharedFlow()
}