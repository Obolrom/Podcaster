package io.obolonsky.network.apihelpers

import com.haroldadmin.cnradapter.NetworkResponse
import io.obolonsky.core.di.data.FeatureToggles
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.FeatureTogglesApi
import io.obolonsky.network.apihelpers.base.CoroutinesApiHelper
import io.obolonsky.network.mappers.FeatureTogglesResponseToFeatureTogglesMapper
import io.obolonsky.network.responses.featuretoggles.FeatureTogglesResponse
import io.obolonsky.network.utils.ProductionTypes
import javax.inject.Inject

class FeatureToggleApiHelper @Inject constructor(
    private val featureTogglesApi: FeatureTogglesApi,
    dispatchers: CoroutineSchedulers,
) : CoroutinesApiHelper<FeatureTogglesResponse, FeatureToggles?, ProductionTypes>(
    dispatchers = dispatchers,
    mapper = FeatureTogglesResponseToFeatureTogglesMapper
) {

    override suspend fun apiRequest(
        param: ProductionTypes
    ): NetworkResponse<FeatureTogglesResponse, *> {
        return featureTogglesApi.getFeatureFlags(param.type)
    }
}