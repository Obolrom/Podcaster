package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import okhttp3.RequestBody
import retrofit2.http.*

interface ShazamCoreApi {

    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: shazam-core.p.rapidapi.com",
    )
    @POST("tracks/recognize")
    suspend fun recognize(
        @Body body: RequestBody
    ): NetworkResponse<ShazamCoreRecognizeResponse, Unit>

    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: shazam-core.p.rapidapi.com"
    )
    @GET("tracks/total-shazams?track_id=469270443")
    suspend fun getTotalShazams(): NetworkResponse<ShazamCoreTotalShazamResponse, Unit>
}