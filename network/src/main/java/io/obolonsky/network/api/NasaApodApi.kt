package io.obolonsky.network.api

import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.nasa.ApodResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApodApi {

    @GET("apod")
    fun getRandomApod(
        @Query("count") apodCount: Int,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY,
    ): Single<List<ApodResponse>>
}