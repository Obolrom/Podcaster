package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.network.api.CoinPaprikaApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.ListCoinFeedItemResponseToListCoinPaprikaMapper
import io.obolonsky.network.responses.coinpaprika.CoinFeedItemResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetCoinFeedApiHelper @Inject constructor(
    private val coinPaprikaApi: CoinPaprikaApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<List<CoinFeedItemResponse>, List<CoinPaprika>, Unit>(
    rxSchedulers = rxSchedulers,
    mapper = ListCoinFeedItemResponseToListCoinPaprikaMapper()
) {

    override fun apiRequest(
        param: Unit
    ): Single<List<CoinFeedItemResponse>> {
        return coinPaprikaApi.getCoinsFeed()
    }
}