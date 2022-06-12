package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class BookPagingResponse(
    @SerializedName("bookId") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("raiting") val raiting: Int,
    @SerializedName("description") val description: String,
    @SerializedName("isFavourite") val isFavorite: Boolean,
)
