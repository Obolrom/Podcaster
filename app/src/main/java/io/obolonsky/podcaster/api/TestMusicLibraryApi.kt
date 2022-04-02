package io.obolonsky.podcaster.api

import io.obolonsky.podcaster.data.responses.BookDetailsResponse
import io.obolonsky.podcaster.data.responses.MediaResponse
import retrofit2.http.GET

interface TestMusicLibraryApi {

    @GET("master/models.json")
    suspend fun getMusic(): MediaResponse

    @GET("details/models.json")
    suspend fun getDetails(/*bookId: Long*/): BookDetailsResponse
}