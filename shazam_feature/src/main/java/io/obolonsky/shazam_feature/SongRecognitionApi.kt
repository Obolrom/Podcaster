package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface SongRecognitionApi {

    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: song-recognition.p.rapidapi.com",
    )
    @POST("song/detect")
    suspend fun detect(
        @Body body: RequestBody
    ): NetworkResponse<SongRecognizeResponse, Unit>
}