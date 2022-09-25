package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.banks.ExchangeRate
import io.obolonsky.core.di.data.banks.mapToCurrency
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.banks.ExchangeRatesResponse

object ExchangeRateResponseToExchangeRateListMapper :
    Mapper<ExchangeRatesResponse, List<ExchangeRate>> {

    override fun map(input: ExchangeRatesResponse): List<ExchangeRate> {
        return input.exchangeRates
            ?.filterNotNull()
            ?.mapNotNull(::mapExchangeRate)
            .orEmpty()
    }

    private fun mapExchangeRate(exchangeRate: ExchangeRatesResponse.RateResponse): ExchangeRate? {
        val baseCurrency = exchangeRate.baseCurrency.mapToCurrency() ?: return null
        val currency = exchangeRate.currency.mapToCurrency() ?: return null
        val purchaseRateNB = exchangeRate.purchaseRateNB ?: return null
        val saleRateNB = exchangeRate.saleRateNB ?: return null

        return ExchangeRate(
            baseCurrency = baseCurrency,
            currency = currency,
            purchaseRate = exchangeRate.purchaseRate,
            purchaseRateNB = purchaseRateNB,
            saleRate = exchangeRate.saleRate,
            saleRateNB = saleRateNB,
        )
    }
}