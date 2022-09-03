package io.obolonsky.spacex.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.spaceX.rocket.Rocket
import io.obolonsky.spacex.usecases.GetRocketFullDetailsUseCase
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class SpaceXViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val getRocketFullDetailsUseCase: GetRocketFullDetailsUseCase,
) : ViewModel() {

    private val internalRocketDetails by lazy {
        MutableSharedFlow<Rocket?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    }
    val rocketDetails: SharedFlow<Rocket?> get() = internalRocketDetails.asSharedFlow()

    fun loadRocketDetails(id: String) {
        viewModelScope.launch {
            when (val response = getRocketFullDetailsUseCase(id)) {
                is Reaction.Success -> {
                    internalRocketDetails.emit(response.data)
                }
                is Reaction.Fail -> {
                    internalRocketDetails.emit(null)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): SpaceXViewModel
    }
}