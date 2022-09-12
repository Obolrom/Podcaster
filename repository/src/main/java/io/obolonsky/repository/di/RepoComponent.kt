package io.obolonsky.repository.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.network.di.components.NetworkComponent
import io.obolonsky.network.di.providers.ApiHelperProviders
import io.obolonsky.repository.database.di.DatabaseComponent
import io.obolonsky.repository.database.di.DatabaseComponentProvider
import io.obolonsky.repository.di.modules.BinderModule

@ApplicationScope
@Component(
    dependencies = [
        ToolsProvider::class,
        ApiHelperProviders::class,
        DatabaseComponentProvider::class,
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
            databaseComponentProvider: DatabaseComponentProvider,
        ): RepoComponent
    }

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
        ): RepoComponent {
            val apiHelperProviders = NetworkComponent.create(toolsProvider)
            val databaseComponentProvider = DatabaseComponent.create(
                toolsProvider = toolsProvider,
            )

            return DaggerRepoComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    apiHelperProviders = apiHelperProviders,
                    databaseComponentProvider = databaseComponentProvider,
                )
        }
    }
}