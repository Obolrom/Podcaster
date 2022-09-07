package io.obolonsky.network.api

import io.obolonsky.network.BuildConfig
import io.obolonsky.network.responses.nasa.MarsPhotoRoverResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MarsPhotosApi {

    /**
     * @param earthDate has format YYYY-MM-DD
     */
    @GET("rovers/{rover_name}/photos")
    fun getPhotosByRover(
        @Path("rover_name") name: String,
        @Query("earth_date") earthDate: String,
        @Query("page") page: Int,
        @Query("api_key") apiKey: String = BuildConfig.NASA_API_KEY,
    ): Single<MarsPhotoRoverResponse>
}