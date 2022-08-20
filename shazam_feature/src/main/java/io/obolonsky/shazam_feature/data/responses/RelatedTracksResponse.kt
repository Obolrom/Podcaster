package io.obolonsky.shazam_feature.data.responses

import com.google.gson.annotations.SerializedName

data class RelatedTracksResponse(
    @SerializedName("tracks") val tracks: List<SongRecognizeResponse.TrackResponse>,
)