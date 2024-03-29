package io.obolonsky.network.responses

import com.google.gson.annotations.SerializedName

data class SongRecognizeResponse(
    @SerializedName("result") val result: Result?,
) {

    data class Result(
        @SerializedName("tagid") val tagId: String?,
        @SerializedName("timestamp") val timestamp: Long?,
        @SerializedName("location") val location: LocationResponse?,
        @SerializedName("track") val track: TrackResponse?,
    )

    data class TrackResponse(
        @SerializedName("subtitle") val subtitle: String,
        @SerializedName("title") val title: String,
        @SerializedName("highlightsurls") val highlightsUrl: HighlightsUrlResponse?, // <-- this link has a lot of interesting stuff
        @SerializedName("relatedtracksurl") val relatedTracksUrl: String?,
        @SerializedName("type") val type: String?,
        @SerializedName("hub") val hub: HubResponse?,
        @SerializedName("images") val images: ImagesResponse?,
        @SerializedName("tagid") val tagId: String?,
        @SerializedName("timestamp") val timestamp: Long?,
        @SerializedName("location") val location: LocationResponse?,
    ) {

        data class ImagesResponse(
            @SerializedName("background") val backgroundUrl: String?,
            @SerializedName("coverart") val coverArtUrl: String?,
            @SerializedName("coverarthq") val coverArtHqUrl: String?,
            @SerializedName("joecolor") val joecolorUrl: String?,
        )

        data class HighlightsUrlResponse(
            @SerializedName("artisthighlightsurl") val artistHighlightsUrl: String?,
            @SerializedName("relatedtracksurl") val relatedTracksUrl: String?, // <-- this link has a lot of interesting stuff
        )

        data class HubResponse(
            @SerializedName("actions") val actions: List<ActionResponse>?,
        ) {

            data class ActionResponse(
                @SerializedName("id") val id: String?,
                @SerializedName("name") val name: String?,
                @SerializedName("type") val type: String?,
                @SerializedName("uri") val uri: String?,
            )
        }
    }

    data class LocationResponse(
        @SerializedName("accuracy") val accuracy: Double?,
        @SerializedName("altitude") val altitude: Int,
        @SerializedName("latitude") val latitude: Int,
        @SerializedName("longitude") val longitude: Int,
    )
}

