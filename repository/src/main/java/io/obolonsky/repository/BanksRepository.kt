package io.obolonsky.repository

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.banks.ExchangeRate
import io.obolonsky.core.di.repositories.BanksRepo
import io.obolonsky.network.apihelpers.GetExchangeRatesHelper
import java.util.*
import javax.inject.Inject

class BanksRepository @Inject constructor(
    private val getExchangeRatesHelper: GetExchangeRatesHelper,
) : BanksRepo {

    override suspend fun getExchangeRatesByDate(date: Date): Reaction<List<ExchangeRate>, Error> {
        return getExchangeRatesHelper.load(date)
    }
}