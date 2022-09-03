package io.obolonsky.spacex.di

import io.obolonsky.spacex.viewmodels.SpaceXViewModel

internal interface AssistedFactoriesProvider {

    fun spaceXViewModelFactory(): SpaceXViewModel.Factory
}