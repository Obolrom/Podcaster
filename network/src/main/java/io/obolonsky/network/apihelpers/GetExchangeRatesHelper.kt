package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.banks.ExchangeRate
import io.obolonsky.network.api.PrivateBankApi
import io.obolonsky.network.mappers.ExchangeRateResponseToExchangeRateListMapper
import io.obolonsky.network.utils.RxSchedulers
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.rx3.await
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetExchangeRatesHelper @Inject constructor(
    private val privateBankApi: PrivateBankApi,
    private val rxSchedulers: RxSchedulers,
) : ApiHelper<List<ExchangeRate>, Date> {

    override suspend fun load(param: Date): Reaction<List<ExchangeRate>, Error> = runWithReaction {
        val date = requireNotNull(
            SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(param)
        )
        privateBankApi.getExchangeRates(date)
            .subscribeOn(rxSchedulers.io)
            .observeOn(rxSchedulers.computation)
            .map(ExchangeRateResponseToExchangeRateListMapper::map)
            .await()
            .orEmpty()
    }
}