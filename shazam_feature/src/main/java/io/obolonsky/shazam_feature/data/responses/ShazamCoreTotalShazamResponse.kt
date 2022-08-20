package io.obolonsky.shazam_feature.data.responses

import com.google.gson.annotations.SerializedName

data class ShazamCoreTotalShazamResponse(
    @SerializedName("id") val id: String
)