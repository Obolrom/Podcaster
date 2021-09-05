package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("data")
    val mediaItems: List<MusicItem>,
)