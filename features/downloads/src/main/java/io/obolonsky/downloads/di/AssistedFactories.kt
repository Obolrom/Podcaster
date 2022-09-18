package io.obolonsky.downloads.di

import io.obolonsky.downloads.viewmodels.DownloadsViewModel

internal interface AssistedFactories {

    fun downloadsViewModelFactory(): DownloadsViewModel.Factory
}