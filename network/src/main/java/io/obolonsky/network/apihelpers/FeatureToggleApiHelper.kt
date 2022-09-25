package io.obolonsky.network.apihelpers

import io.obolonsky.core.di.Error
import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.FeatureToggles
import io.obolonsky.core.di.utils.CoroutineSchedulers
import io.obolonsky.network.api.FeatureTogglesApi
import io.obolonsky.network.mappers.FeatureTogglesResponseToFeatureTogglesMapper
import io.obolonsky.network.utils.ProductionTypes
import io.obolonsky.network.utils.runWithReaction
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FeatureToggleApiHelper @Inject constructor(
    private val featureTogglesApi: FeatureTogglesApi,
    private val dispatchers: CoroutineSchedulers,
) : ApiHelper<FeatureToggles?, ProductionTypes> {

    override suspend fun load(param: ProductionTypes): Reaction<FeatureToggles?, Error> {
        return featureTogglesApi.getFeatureFlags(param.type).runWithReaction {
            withContext(dispatchers.computation) {
                FeatureTogglesResponseToFeatureTogglesMapper.map(this@runWithReaction)
            }
        }
    }
}