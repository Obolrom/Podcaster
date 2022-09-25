package io.obolonsky.storage.di

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
        BinderModule::class,
        DownloadsModule::class,
    ]
)
interface StorageComponent : StorageProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
        ): StorageComponent
    }

    companion object {

        fun createStorageProvider(
            toolsProvider: ToolsProvider,
        ) = DaggerStorageComponent.factory()
            .create(
                toolsProvider = toolsProvider,
            )
    }
}