package io.obolonsky.network.api

import io.obolonsky.network.responses.banks.ExchangeRatesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivateBankApi {

    @GET("exchange_rates?json")
    fun getExchangeRates(
        @Query("date") date: String
    ): Single<ExchangeRatesResponse>
}