package io.obolonsky.shazam.di

import io.obolonsky.core.di.repositories.ShazamRepo
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
class ScopedShazamRepo @Inject constructor(
    private val shazamRepo: ShazamRepo,
) : ShazamRepo by shazamRepo