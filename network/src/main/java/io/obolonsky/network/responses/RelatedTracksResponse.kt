package io.obolonsky.network.responses

import com.google.gson.annotations.SerializedName

data class RelatedTracksResponse(
    @SerializedName("result") val result: Result?,
) {

    data class Result(
        @SerializedName("tracks") val tracks: List<SongRecognizeResponse.TrackResponse>?,
    )
}