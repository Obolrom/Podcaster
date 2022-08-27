package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.responses.RelatedTracksResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface PlainShazamApi {

    @GET
    suspend fun getRelatedTracks(@Url url: String): NetworkResponse<RelatedTracksResponse, Unit>
}