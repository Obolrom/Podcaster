package io.obolonsky.shazam_feature.di

import dagger.Module
import io.obolonsky.shazam_feature.viewmodels.RecorderViewModel
import io.obolonsky.shazam_feature.viewmodels.ShazamViewModel

@Module
internal interface AssistedFactoriesModule {

    fun shazamViewModel(): ShazamViewModel.Factory

    fun recorderViewModel(): RecorderViewModel.Factory
}