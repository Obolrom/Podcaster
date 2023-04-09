package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.RelatedTracksResponse
import io.obolonsky.network.responses.SongRecognizeResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface SongRecognitionApi {

    @Headers(
        "X-RapidAPI-Key: ${BuildConfig.SHAZAM_API_KEY}",
        "X-RapidAPI-Host: shazam-song-recognizer.p.rapidapi.com",
    )
    @POST("recognize")
    suspend fun detect(
        @Body body: RequestBody
    ): NetworkResponse<SongRecognizeResponse, Unit>

    @Headers(
        "X-RapidAPI-Key: ${BuildConfig.SHAZAM_API_KEY}",
        "X-RapidAPI-Host: shazam-song-recognizer.p.rapidapi.com",
    )
    @GET
    suspend fun getRelatedTracks(
        @Url url: String,
    ): NetworkResponse<RelatedTracksResponse, Unit>
}