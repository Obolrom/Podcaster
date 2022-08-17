package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.http.*

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