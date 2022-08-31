package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.ShazamDetectResponse
import io.obolonsky.network.responses.ShazamSearchResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface ShazamApi {

    @Headers(
        "X-RapidAPI-Key: ${BuildConfig.SHAZAM_API_KEY}",
        "X-RapidAPI-Host: shazam.p.rapidapi.com"
    )
    @GET("search?locale=en-US&offset=0&limit=5")
    suspend fun searchByQuery(
        @Query("term") searchQuery: String
    ): NetworkResponse<ShazamSearchResponse, Unit>

    @Headers(
        "X-RapidAPI-Key: ${BuildConfig.SHAZAM_API_KEY}",
        "X-RapidAPI-Host: shazam.p.rapidapi.com",
        "content-type: text/plain"
    )
    @POST("songs/detect")
    suspend fun detect(
        @Body rawAudioRequestBody: RequestBody
    ): NetworkResponse<ShazamDetectResponse, Unit>
}