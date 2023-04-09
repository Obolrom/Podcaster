package io.obolonsky.network.api

import io.obolonsky.network.responses.coinpaprika.CoinDetailsResponse
import io.obolonsky.network.responses.coinpaprika.CoinFeedItemResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinPaprikaApi {

    // https://api.coinpaprika.com/v1/price-converter?base_currency_id=btc-bitcoin&quote_currency_id=eth-ethereum&amount=5

    @GET("coins")
    fun getCoinsFeed(): Single<List<CoinFeedItemResponse>>

    @GET("coins/{coin_id}")
    fun getCoinDetailsById(
        @Path("coin_id") coinId: String,
    ): Single<CoinDetailsResponse>
}