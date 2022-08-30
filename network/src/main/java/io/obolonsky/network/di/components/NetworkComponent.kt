package io.obolonsky.network.di.components

import dagger.Component
import io.obolonsky.core.di.depsproviders.CoroutineSchedulersProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.network.di.modules.NetworkModule
import io.obolonsky.network.di.providers.NetworkClientsProvider

@Component(
    dependencies = [
        ToolsProvider::class
    ],
    modules = [
        NetworkModule::class,
    ]
)
interface NetworkComponent : NetworkClientsProvider, CoroutineSchedulersProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
        ): NetworkComponent
    }

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
        ): NetworkComponent {
            return DaggerNetworkComponent.factory()
                .create(toolsProvider)
        }
    }
}