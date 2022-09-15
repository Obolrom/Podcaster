package io.obolonsky.nasa.di

import io.obolonsky.nasa.viewmodels.NasaViewModel

internal interface AssistedFactoriesProvider {

    fun nasaViewModelFactory(): NasaViewModel.Factory
}