package io.obolonsky.shazam_feature

import com.google.gson.annotations.SerializedName

data class ShazamCoreRecognizeResponse(
    @SerializedName("tagid") val tagId: String,
    @SerializedName("track") val track: TrackResponse?,
) {

    data class TrackResponse(
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("title") val title: String,
    )
}
