package io.obolonsky.crypto.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.CryptoRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        CryptoRepoProvider::class,
    ]
)
internal interface CryptoComponent : ViewModelProviders {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            cryptoRepoProvider: CryptoRepoProvider,
        ): CryptoComponent
    }

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
            cryptoRepoProvider: CryptoRepoProvider,
        ): CryptoComponent {
            return DaggerCryptoComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    cryptoRepoProvider = cryptoRepoProvider,
                )
        }
    }
}