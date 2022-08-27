package io.obolonsky.repository.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.network.di.DaggerNetworkComponent
import io.obolonsky.network.di.NetworkClientsProvider
import io.obolonsky.repository.di.modules.BinderModule

@Component(
    dependencies = [
        ToolsProvider::class,
        NetworkClientsProvider::class,
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
            networkClientsProvider: NetworkClientsProvider,
        ): RepoComponent
    }

    companion object {

        fun create(toolsProvider: ToolsProvider): RepoComponent {
            val networkComponent = DaggerNetworkComponent.create()

            return DaggerRepoComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    networkClientsProvider = networkComponent
                )
        }
    }
}