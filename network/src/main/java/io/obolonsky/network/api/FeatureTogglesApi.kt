package io.obolonsky.network.api

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.network.responses.featuretoggles.FeatureTogglesResponse
import retrofit2.http.GET
import retrofit2.http.Part

interface FeatureTogglesApi {

    @GET("{production_type}/feature_flags.json")
    suspend fun getFeatureFlags(
        @Part("production_type") productionType: String,
    ): NetworkResponse<FeatureTogglesResponse, Unit>
}