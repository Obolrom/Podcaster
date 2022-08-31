package io.obolonsky.network.di.providers

import io.obolonsky.network.api.FeatureTogglesApi

interface FeatureTogglesApiProvider {

    val featureTogglesApi: FeatureTogglesApi
}