package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import kotlinx.coroutines.flow.Flow

interface CryptoRepo {

    fun getCoinFeed(): Flow<Reaction<List<CoinPaprika>>>

    suspend fun getCoinDetails(coinId: String): Reaction<CoinPaprika>
}