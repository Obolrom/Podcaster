package io.obolonsky.podcaster.data.responses

import com.google.gson.annotations.SerializedName

data class BookDetailsResponse(
    @SerializedName("bookId") val id: String,
    @SerializedName("bookTitle") val bookTitle: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("category") val category: String,
    @SerializedName("duration") val duration: Long,
    @SerializedName("bookAuthor") val bookAuthor: BookAuthorResponse,
    @SerializedName("voiceOverAuthor") val voiceOverAuthor: VoiceOverAuthorResponse,
    @SerializedName("raiting") val raiting: Int,
    @SerializedName("description") val description: String,
    @SerializedName("auditionsCount") val auditionsCount: Int,
    @SerializedName("lastChapter") val lastChapter: ChapterResponse?,
    @SerializedName("chapters") val chapters: List<ChapterResponse>
) {

    data class BookAuthorResponse(
        @SerializedName("bookAuthorId") val id: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("raiting") val raiting: Int,
    )

    data class VoiceOverAuthorResponse(
        @SerializedName("voiceOverAuthorId") val id: String,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("userName") val userName: String,
        @SerializedName("email") val email: String,
        @SerializedName("raiting") val raiting: Int,
    )

    data class ChapterResponse(
        @SerializedName("chapterId") val id: String,
        @SerializedName("bookId") val bookId: String,
        @SerializedName("chapterName") val chapterName: String,
        @SerializedName("imageUrl") val imageUrl: String,
        @SerializedName("mediaUrl") val mediaUrl: String,
        @SerializedName("duration") val duration: Long,
        @SerializedName("lastTimeStamp") val lastTimeStamp: Long?,
    )
}
