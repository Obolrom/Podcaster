package io.obolonsky.shazam.di

import dagger.Module
import io.obolonsky.shazam.viewmodels.ShazamViewModel

@Module
internal interface AssistedFactoriesModule {

    fun shazamViewModel(): ShazamViewModel.Factory
}