package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class MusicItem(
    @SerializedName("song_name")
    val title: String,

    @SerializedName("id")
    val id: Long,

    @SerializedName("url")
    val mediaUrl: String,
)