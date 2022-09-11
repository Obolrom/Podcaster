package io.obolonsky.nasa.di

import io.obolonsky.core.di.repositories.NasaRepo
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
internal class ScopedNasaRepo @Inject constructor(
    nasaRepo: NasaRepo,
) : NasaRepo by nasaRepo