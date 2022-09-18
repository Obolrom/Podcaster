package io.obolonsky.downloads.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.utils.CoroutineSchedulers

class DownloadsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {


    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): DownloadsViewModel
    }
}