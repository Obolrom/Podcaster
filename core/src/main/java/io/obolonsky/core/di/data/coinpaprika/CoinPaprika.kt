package io.obolonsky.core.di.data.coinpaprika

data class CoinPaprika(
    val id: String,
    val isActive: Boolean,
    val isNew: Boolean?,
    val name: String,
    val rank: Int,
    val symbol: String?,
    val type: String?
)
