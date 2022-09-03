package io.obolonsky.spacex.di

import io.obolonsky.core.di.repositories.SpaceXRepo
import io.obolonsky.core.di.scopes.FeatureScope
import javax.inject.Inject

@FeatureScope
internal class ScopedSpaceXRepo @Inject constructor(
    spaceXRepo: SpaceXRepo
) : SpaceXRepo by spaceXRepo