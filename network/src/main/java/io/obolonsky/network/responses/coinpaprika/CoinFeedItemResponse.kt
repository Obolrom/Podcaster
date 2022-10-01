package io.obolonsky.network.responses.coinpaprika

import com.google.gson.annotations.SerializedName

data class CoinFeedItemResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("is_active")
    val isActive: Boolean?,
    @SerializedName("is_new")
    val isNew: Boolean?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("rank")
    val rank: Int?,
    @SerializedName("symbol")
    val symbol: String?,
    @SerializedName("type")
    val type: String?
)