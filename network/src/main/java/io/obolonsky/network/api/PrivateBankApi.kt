package io.obolonsky.network.api

import io.obolonsky.network.responses.banks.ExchangeRatesResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivateBankApi {

//    https://api.privatbank.ua/p24api/exchange_rates?json&date=16.09.2022
    @GET("exchange_rates?json")
    fun getExchangeRates(
        @Query("date") date: String
    ): Single<ExchangeRatesResponse>
}