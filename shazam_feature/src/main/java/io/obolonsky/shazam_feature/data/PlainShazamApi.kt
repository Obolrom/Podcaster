package io.obolonsky.shazam_feature.data

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.shazam_feature.data.responses.RelatedTracksResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface PlainShazamApi {

    @GET
    suspend fun getRelatedTracks(@Url url: String): NetworkResponse<RelatedTracksResponse, Unit>
}