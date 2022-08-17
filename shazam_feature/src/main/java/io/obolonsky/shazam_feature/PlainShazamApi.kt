package io.obolonsky.shazam_feature

import com.haroldadmin.cnradapter.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface PlainShazamApi {

    @GET
    suspend fun getRelatedTracks(@Url url: String): NetworkResponse<RelatedTracksResponse, Unit>
}