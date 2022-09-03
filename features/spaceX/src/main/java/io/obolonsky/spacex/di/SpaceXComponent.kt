package io.obolonsky.spacex.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope

@FeatureScope
@Component(
    dependencies = [
        ApplicationProvider::class,
    ],
)
internal interface SpaceXComponent : AssistedFactoriesProvider {

    @Component.Factory
    interface Factory {

        fun create(applicationProvider: ApplicationProvider): SpaceXComponent
    }

    companion object {

        fun create(applicationProvider: ApplicationProvider): SpaceXComponent {
            return DaggerSpaceXComponent.factory()
                .create(applicationProvider)
        }
    }
}