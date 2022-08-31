package io.obolonsky.repository

import io.obolonsky.core.di.Reaction
import io.obolonsky.core.di.data.FeatureToggles
import io.obolonsky.core.di.repositories.FeatureTogglesRepo
import io.obolonsky.network.apihelpers.FeatureToggleApiHelper
import io.obolonsky.network.utils.ProductionTypes
import javax.inject.Inject

class FeatureTogglesRepository @Inject constructor(
    private val featureToggleApiHelper: FeatureToggleApiHelper,
) : FeatureTogglesRepo {

    override suspend fun getFeatureToggles(): FeatureToggles {
        return when (val result = featureToggleApiHelper.load(ProductionTypes.PROD)) {
            is Reaction.Success -> {
                // TODO: return value from cache
                result.data ?: FeatureToggles(
                    FeatureToggles.Shazam(false)
                )
            }

            is Reaction.Fail -> {
                FeatureToggles(
                    shazam = FeatureToggles.Shazam(
                        isWorking = true
                    ),
                )
            }
        }
    }
}