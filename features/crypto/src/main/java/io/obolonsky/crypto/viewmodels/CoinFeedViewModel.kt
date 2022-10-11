package io.obolonsky.crypto.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.core.di.utils.reactWith
import io.obolonsky.crypto.di.ScopedCryptoRepo
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

internal class CoinFeedViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineSchedulers,
    private val cryptoRepo: ScopedCryptoRepo,
) : ViewModel() {

    private val internalCoinFeed by lazy {
        MutableSharedFlow<List<CoinPaprika>>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val coinFeed: SharedFlow<List<CoinPaprika>> get() = internalCoinFeed.asSharedFlow()

    fun loadFeed() {
        cryptoRepo.getCoinFeed()
            .reactWith(
                { internalCoinFeed.emit(it) },
                { }
            )
            .flowOn(dispatchers.computation)
            .launchIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): CoinFeedViewModel
    }
}