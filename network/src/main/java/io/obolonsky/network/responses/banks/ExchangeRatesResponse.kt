package io.obolonsky.network.responses.banks

import com.google.gson.annotations.SerializedName

data class ExchangeRatesResponse(
    @SerializedName("date") val date: String?,
    @SerializedName("bank") val bankTitle: String?,
    @SerializedName("baseCurrency") val baseCurrency: Int?,
    @SerializedName("exchangeRate") val exchangeRates: List<RateResponse?>?,
) {

    data class RateResponse(
        @SerializedName("baseCurrency") val baseCurrency: String?,
        @SerializedName("currency") val currency: String?,
        @SerializedName("purchaseRate") val purchaseRate: Double?,
        @SerializedName("purchaseRateNB") val purchaseRateNB: Double?,
        @SerializedName("saleRate") val saleRate: Double?,
        @SerializedName("saleRateNB") val saleRateNB: Double?,
    )
}