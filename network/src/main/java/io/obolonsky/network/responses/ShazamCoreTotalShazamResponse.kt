package io.obolonsky.network.responses

import com.google.gson.annotations.SerializedName

data class ShazamCoreTotalShazamResponse(
    @SerializedName("id") val id: String
)