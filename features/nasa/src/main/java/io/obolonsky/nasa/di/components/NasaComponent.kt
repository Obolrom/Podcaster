package io.obolonsky.nasa.di.components

import dagger.Component
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.NasaRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.nasa.di.AssistedFactoriesProvider
import io.obolonsky.nasa.ui.NasaActivity

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        NasaRepoProvider::class,
    ]
)
internal interface NasaComponent : AssistedFactoriesProvider {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            nasaRepoProvider: NasaRepoProvider,
        ): NasaComponent
    }

    fun inject(target: NasaActivity)

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
            nasaRepoProvider: NasaRepoProvider,
        ): NasaComponent {
            return DaggerNasaComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    nasaRepoProvider = nasaRepoProvider,
                )
        }
    }
}