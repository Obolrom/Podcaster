package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.SongRecognizeResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SongRecognitionApi {

    @Headers(
        "X-RapidAPI-Key: ${BuildConfig.SHAZAM_API_KEY}",
        "X-RapidAPI-Host: song-recognition.p.rapidapi.com",
    )
    @POST("song/detect")
    suspend fun detect(
        @Body body: RequestBody
    ): NetworkResponse<SongRecognizeResponse, Unit>
}