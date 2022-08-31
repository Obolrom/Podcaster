package io.obolonsky.core.di.repositories

import io.obolonsky.core.di.data.FeatureToggles

interface FeatureTogglesRepo {

    suspend fun getFeatureToggles(): FeatureToggles
}