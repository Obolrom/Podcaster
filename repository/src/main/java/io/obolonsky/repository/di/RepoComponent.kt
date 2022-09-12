package io.obolonsky.repository.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.network.di.components.NetworkComponent
import io.obolonsky.network.di.providers.ApiHelperProviders
import io.obolonsky.repository.di.modules.BinderModule
import io.obolonsky.storage.di.StorageComponent
import io.obolonsky.storage.di.StorageProvider

@ApplicationScope
@Component(
    dependencies = [
        ToolsProvider::class,
        ApiHelperProviders::class,
        StorageProvider::class,
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
            storageProvider: StorageProvider,
        ): RepoComponent
    }

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
        ): RepoComponent {
            val apiHelperProviders = NetworkComponent.create(toolsProvider)
            val storageProvider = StorageComponent.createStorageProvider(toolsProvider)

            return DaggerRepoComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    apiHelperProviders = apiHelperProviders,
                    storageProvider = storageProvider,
                )
        }
    }
}