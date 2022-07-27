package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface ShazamApi {

    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: shazam.p.rapidapi.com"
    )
    @GET("search?locale=en-US&offset=0&limit=5")
    suspend fun searchByQuery(
        @Query("term") searchQuery: String
    ): NetworkResponse<ShazamSearchResponse, Unit>

    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: shazam.p.rapidapi.com",
        "content-type: text/plain"
    )
    @POST("songs/detect")
    suspend fun detect(
        @Body rawAudioRequestBody: RequestBody
    ): NetworkResponse<ShazamDetectResponse, Unit>
}