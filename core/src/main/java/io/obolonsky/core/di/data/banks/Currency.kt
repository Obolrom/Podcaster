package io.obolonsky.core.di.data.banks

enum class Currency {
    UAH,
    EUR,
    USD,
    CHF,
    SEK,
}

fun String?.mapToCurrency() = when (this) {
    Currency.UAH.name -> Currency.UAH

    Currency.EUR.name -> Currency.EUR

    Currency.USD.name -> Currency.USD

    Currency.CHF.name -> Currency.CHF

    Currency.SEK.name -> Currency.SEK

    else -> null
}