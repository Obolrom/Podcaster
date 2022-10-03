package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.network.api.CoinPaprikaApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.CoinDetailsResponseToCoinPaprikaMapper
import io.obolonsky.network.responses.coinpaprika.CoinDetailsResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetCoinDetailsApiHelper @Inject constructor(
    private val coinPaprikaApi: CoinPaprikaApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<CoinDetailsResponse, CoinPaprika, GetCoinDetailsApiHelper.QueryParams>(
    rxSchedulers = rxSchedulers,
    mapper = CoinDetailsResponseToCoinPaprikaMapper()
) {

    override fun apiRequest(param: QueryParams): Single<CoinDetailsResponse> {
        return coinPaprikaApi.getCoinDetailsById(param.coinId)
    }

    data class QueryParams(
        val coinId: String,
    )
}