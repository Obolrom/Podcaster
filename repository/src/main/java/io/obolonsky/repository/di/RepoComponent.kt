package io.obolonsky.repository.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.network.di.components.DaggerNetworkComponent
import io.obolonsky.network.di.providers.ApiHelperProviders
import io.obolonsky.repository.di.modules.BinderModule

@Component(
    dependencies = [
        ToolsProvider::class,
        ApiHelperProviders::class,
    ],
    modules = [
        BinderModule::class
    ]
)
interface RepoComponent : RepositoryProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            apiHelperProviders: ApiHelperProviders,
        ): RepoComponent
    }

    companion object {

        fun create(toolsProvider: ToolsProvider): RepoComponent {
            val apiHelperProviders = DaggerNetworkComponent.factory()
                .create(toolsProvider)

            return DaggerRepoComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    apiHelperProviders = apiHelperProviders,
                )
        }
    }
}