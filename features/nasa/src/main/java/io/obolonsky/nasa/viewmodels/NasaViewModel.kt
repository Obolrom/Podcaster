package io.obolonsky.nasa.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.reactWithSuccessOrDefault
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.nasa.usecases.GetApodImageUrlsUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class NasaViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val apodImageUrlsUseCase: GetApodImageUrlsUseCase,
    private val dispatchers: CoroutineSchedulers,
) : ViewModel() {

    private val internalApodImageUrls by lazy {
        MutableSharedFlow<List<String>>()
    }
    val apodImageUrls: SharedFlow<List<String>> get() = internalApodImageUrls.asSharedFlow()

    fun loadApodImageUrls(imageCount: Int) {
        viewModelScope.launch(dispatchers.computation) {
            apodImageUrlsUseCase(imageCount)
                .reactWithSuccessOrDefault { emptyList() }
                .let { internalApodImageUrls.emit(it) }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): NasaViewModel
    }
}