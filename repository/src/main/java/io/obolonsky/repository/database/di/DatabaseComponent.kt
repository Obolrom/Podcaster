package io.obolonsky.repository.database.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.scopes.ApplicationScope

@ApplicationScope
@Component(
    dependencies = [
        ToolsProvider::class,
    ],
    modules = [
        DatabaseModule::class,
    ]
)
interface DatabaseComponent : DatabaseComponentProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
        ): DatabaseComponent
    }

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
        ) = DaggerDatabaseComponent.factory()
            .create(
                 toolsProvider = toolsProvider,
            )
    }
}