package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.data.banks.ExchangeRate
import io.obolonsky.network.api.PrivateBankApi
import io.obolonsky.network.apihelpers.base.RxApiHelper
import io.obolonsky.network.mappers.ExchangeRateResponseToExchangeRateListMapper
import io.obolonsky.network.responses.banks.ExchangeRatesResponse
import io.obolonsky.network.utils.RxSchedulers
import io.reactivex.rxjava3.core.Single
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetExchangeRatesHelper @Inject constructor(
    private val privateBankApi: PrivateBankApi,
    rxSchedulers: RxSchedulers,
) : RxApiHelper<ExchangeRatesResponse, List<ExchangeRate>, Date>(
    rxSchedulers = rxSchedulers,
    mapper = ExchangeRateResponseToExchangeRateListMapper
) {

    override fun apiRequest(param: Date): Single<ExchangeRatesResponse> {
        val date = requireNotNull(
            SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).format(param)
        )
        return privateBankApi.getExchangeRates(date)
    }

    override fun List<ExchangeRate>?.onNullableReturn(): List<ExchangeRate> = orEmpty()
}