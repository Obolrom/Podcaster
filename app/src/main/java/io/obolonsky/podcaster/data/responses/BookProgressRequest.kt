package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class BookProgressRequest(
    @SerializedName("BookId") val bookId: String,
    @SerializedName("UserId") val userId: String,
    @SerializedName("ChapterId") val chapterId: String,
    @SerializedName("LastTimeStamp") val progressTimestamp: Long,
)