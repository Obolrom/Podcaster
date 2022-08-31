package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.FeatureToggles
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.featuretoggles.FeatureTogglesResponse

object FeatureTogglesResponseToFeatureTogglesMapper :
    Mapper<FeatureTogglesResponse, FeatureToggles?> {

    override fun map(input: FeatureTogglesResponse): FeatureToggles? {
        val shazamIsWorking = input.shazam?.isWorking ?: return null

        return FeatureToggles(
            shazam = FeatureToggles.Shazam(
                isWorking = shazamIsWorking
            )
        )
    }
}