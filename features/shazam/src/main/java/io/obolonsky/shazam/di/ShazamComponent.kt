package io.obolonsky.shazam.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.DownloadsActionProvider
import io.obolonsky.core.di.depsproviders.PlayerActionProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.ShazamRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.shazam.ui.ShazamActivity

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        ShazamRepoProvider::class,
        PlayerActionProvider::class,
        DownloadsActionProvider::class,
    ],
)
internal interface ShazamComponent : AssistedFactoriesModule {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            shazamRepoProvider: ShazamRepoProvider,
            playerActionProvider: PlayerActionProvider,
            downloadsActionProvider: DownloadsActionProvider,
        ): ShazamComponent
    }

    fun inject(target: ShazamActivity)
}