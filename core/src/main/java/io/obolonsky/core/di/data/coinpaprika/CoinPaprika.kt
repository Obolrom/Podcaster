package io.obolonsky.core.di.data.coinpaprika

data class CoinPaprika(
    val id: String,
    val isActive: Boolean,
    val isNew: Boolean?,
    val name: String,
    val logo: String?,
    val rank: Int,
    val symbol: String?,
    val type: String?,
    val tags: List<Tag>,
) {

    data class Tag(
        val id: String,
        val name: String,
        val coinCounter: Int,
        val icoCounter: Int?,
    )
}