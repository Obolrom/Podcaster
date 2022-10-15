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

class CoinDetailsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val dispatchers: CoroutineSchedulers,
    private val cryptoRepo: ScopedCryptoRepo,
) : ViewModel() {

    private val internalCoinDetails by lazy {
        MutableSharedFlow<CoinPaprika>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }
    val coinDetails: SharedFlow<CoinPaprika> get() = internalCoinDetails.asSharedFlow()

    fun loadDetails() {
        val coinId = savedStateHandle.get<String>(ID_KEY) ?: return

        flow { emit(cryptoRepo.getCoinDetails(coinId)) }
            .reactWith(
                {
                    internalCoinDetails.emit(it)
                },
                { }
            )
            .flowOn(dispatchers.computation)
            .launchIn(viewModelScope)
    }

    @AssistedFactory
    interface Factory {

        fun create(savedStateHandle: SavedStateHandle): CoinDetailsViewModel
    }

    companion object {

        const val ID_KEY = "id_key"
    }
}