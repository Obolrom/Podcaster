package io.obolonsky.network.responses

import com.google.gson.annotations.SerializedName

data class ShazamDetectResponse(
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("tagid") val tagId: String,
    @SerializedName("track") val track: TrackResponse,
) {

    data class TrackResponse(
        @SerializedName("title") val title: String,
        @SerializedName("subtitle") val subtitle: String,
    )
}