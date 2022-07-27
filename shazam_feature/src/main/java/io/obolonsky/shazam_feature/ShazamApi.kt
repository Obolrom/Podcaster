package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface ShazamApi {

    // TODO: change response
    @Headers(
        "X-RapidAPI-Key: a8a51e0479mshf32be59d72d04c9p1b52cdjsn5c4ad265ea7f",
        "X-RapidAPI-Host: shazam.p.rapidapi.com"
    )
    @GET("search?term=kiss%20the%20rain&locale=en-US&offset=0&limit=5")
    suspend fun searchByQuery(
//        @Query("term") searchQuery: String
    ): NetworkResponse<ShazamSearchResponse, Unit>
}