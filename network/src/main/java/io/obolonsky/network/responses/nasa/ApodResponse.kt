package io.obolonsky.network.responses.nasa

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("date") val date: String?,
    @SerializedName("explanation") val explanation: String?,
    @SerializedName("hdurl") val hdUrl: String?,
    @SerializedName("media_type") val mediaType: String?,
    @SerializedName("url") val url: String?,
)
