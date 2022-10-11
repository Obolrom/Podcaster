package io.obolonsky.repository

import dagger.Reusable
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.repositories.CryptoRepo
import io.obolonsky.network.apihelpers.GetCoinDetailsApiHelper
import io.obolonsky.network.apihelpers.GetCoinFeedApiHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Reusable
class CryptoRepository @Inject constructor(
    private val getCoinFeedApiHelper: GetCoinFeedApiHelper,
    private val getCoinDetailsApiHelper: GetCoinDetailsApiHelper,
) : CryptoRepo {

    override fun getCoinFeed(): Flow<Reaction<List<CoinPaprika>>> {
        return flow { emit(getCoinFeedApiHelper.load(Unit)) }
    }

    override suspend fun getCoinDetails(coinId: String): Reaction<CoinPaprika> {
        return getCoinDetailsApiHelper.load(
            param = GetCoinDetailsApiHelper.QueryParams(
                coinId = coinId,
            )
        )
    }
}