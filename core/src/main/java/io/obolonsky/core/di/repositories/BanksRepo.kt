package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.banks.ExchangeRate
import java.util.*

interface BanksRepo {

    suspend fun getExchangeRatesByDate(date: Date): Reaction<List<ExchangeRate>, Error>
}