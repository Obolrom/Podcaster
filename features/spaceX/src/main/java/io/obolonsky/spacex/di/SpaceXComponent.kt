package io.obolonsky.spacex.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.NetworkStatusObservableProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.SpaceXRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.spacex.ui.SpaceXActivity

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        SpaceXRepoProvider::class,
        NetworkStatusObservableProvider::class,
    ],
)
internal interface SpaceXComponent : AssistedFactoriesProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            spaceXRepoProvider: SpaceXRepoProvider,
            networkStatusObservableProvider: NetworkStatusObservableProvider,
        ): SpaceXComponent
    }

    fun inject(target: SpaceXActivity)

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
            spaceXRepoProvider: SpaceXRepoProvider,
            networkStatusObservableProvider: NetworkStatusObservableProvider,
        ): SpaceXComponent {
            return DaggerSpaceXComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    spaceXRepoProvider = spaceXRepoProvider,
                    networkStatusObservableProvider,
                )
        }
    }
}