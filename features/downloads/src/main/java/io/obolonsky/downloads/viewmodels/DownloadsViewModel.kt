package io.obolonsky.downloads.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.downloads.usecases.GetTracksFlowUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DownloadsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val getTracksFlowUseCase: GetTracksFlowUseCase,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    suspend fun getTracks() = withContext(dispatchers.computation) {
        getTracksFlowUseCase()
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): DownloadsViewModel
    }
}