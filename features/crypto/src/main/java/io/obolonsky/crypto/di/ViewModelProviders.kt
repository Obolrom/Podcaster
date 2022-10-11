package io.obolonsky.crypto.di

import io.obolonsky.crypto.viewmodels.CoinDetailsViewModel
import io.obolonsky.crypto.viewmodels.CoinFeedViewModel

internal interface ViewModelProviders {

    fun coinFeedViewModelFactory(): CoinFeedViewModel.Factory

    fun coinDetailViewModelFactory(): CoinDetailsViewModel.Factory
}