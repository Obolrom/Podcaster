package io.obolonsky.shazam_feature

import com.google.gson.annotations.SerializedName

data class ShazamCoreRecognizeResponse(
    @SerializedName("tagid") val tagId: String?,
    @SerializedName("timestamp") val timestamp: Long?,
    @SerializedName("track") val track: TrackResponse?,
    @SerializedName("location") val location: LocationResponse?,
) {

    data class TrackResponse(
        @SerializedName("subtitle") val subtitle: String?,
        @SerializedName("title") val title: String?,
        @SerializedName("highlightsurls") val highlightsUrl: HighlightsUrlResponse?,
        @SerializedName("type") val type: String?,
        @SerializedName("hub") val hub: HubResponse?,
    ) {

        data class HighlightsUrlResponse(
            @SerializedName("artisthighlightsurl") val artisthighlightsurl: String?,
            @SerializedName("relatedtracksurl") val relatedTracksUrl: String?,
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
