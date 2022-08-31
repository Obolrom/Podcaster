package io.obolonsky.core.di.repositories.providers

import io.obolonsky.core.di.repositories.FeatureTogglesRepo

interface FeatureTogglesRepoProvider {

    val featureTogglesRepo: FeatureTogglesRepo
}