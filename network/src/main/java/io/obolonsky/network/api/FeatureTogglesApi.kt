package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.responses.featuretoggles.FeatureTogglesResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FeatureTogglesApi {

    @GET("{production_type}/feature_flags.json")
    suspend fun getFeatureFlags(
        @Path("production_type") productionType: String,
    ): NetworkResponse<FeatureTogglesResponse, Unit>
}