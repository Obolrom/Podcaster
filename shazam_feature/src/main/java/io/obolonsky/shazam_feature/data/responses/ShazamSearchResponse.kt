package io.obolonsky.shazam_feature.data.responses

import com.google.gson.annotations.SerializedName

data class ShazamSearchResponse(
    @SerializedName("tracks") val tracks: TracksResponse,
    @SerializedName("artists") val artists: ArtistsResponse,
) {

    data class TracksResponse(
        @SerializedName("hits") val hits: List<HitResponse>,
    ) {

        data class HitResponse(
            @SerializedName("snippet") val snippet: String?,
        )
    }

    data class ArtistsResponse(
        @SerializedName("hits") val hits: List<HitResponse>,
    ) {

        data class HitResponse(
            @SerializedName("artist") val artists: ArtistResponse,
        ) {

            data class ArtistResponse(
                @SerializedName("id") val id: String?,
            )
        }
    }
}