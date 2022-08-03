package io.obolonsky.shazam_feature

import com.google.gson.annotations.SerializedName

data class ShazamCoreRecognizeResponse(
    @SerializedName("tagid") val tagId: String,
)
