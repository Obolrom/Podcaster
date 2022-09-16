package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.banks.ExchangeRate
import io.obolonsky.network.api.PrivateBankApi
import io.obolonsky.network.mappers.ExchangeRateResponseToExchangeRateListMapper
import io.obolonsky.network.utils.RxSchedulers
import kotlinx.coroutines.rx3.await
import javax.inject.Inject

class GetExchangeRatesHelper @Inject constructor(
    private val privateBankApi: PrivateBankApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<List<ExchangeRate>, String> {

    override suspend fun load(param: String): Reaction<List<ExchangeRate>, Error> = try {
        val result = privateBankApi.getExchangeRates(param)
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(ExchangeRateResponseToExchangeRateListMapper::map)
            .await()
            .orEmpty()

        Reaction.Success(result)
    } catch (e: Exception) {
        Reaction.Fail(Error.NetworkError(e))
    }
}