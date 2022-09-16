package io.obolonsky.core.di.data.banks

data class ExchangeRate(
    val baseCurrency: Currency,
    val currency: Currency,
    val purchaseRate: Double?,
    val purchaseRateNB: Double,
    val saleRate: Double?,
    val saleRateNB: Double,
)