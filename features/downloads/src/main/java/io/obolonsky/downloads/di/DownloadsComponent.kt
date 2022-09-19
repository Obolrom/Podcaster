package io.obolonsky.downloads.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.*
import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider
import io.obolonsky.core.di.repositories.providers.DownloadsRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.downloads.MediaDownloadService
import io.obolonsky.downloads.ui.DownloadsActivity

@FeatureScope
@Component(
    dependencies = [
        ToolsProvider::class,
        PlayerActionProvider::class,
        NasaActionsProvider::class,
        NetworkStatusObservableProvider::class,
        DownloadsActionProvider::class,
        DownloadsRepoProvider::class,
        DownloadsStorageProvider::class,
    ],
)
internal interface DownloadsComponent : AssistedFactories {

    @Component.Factory
    interface Factory {

        fun create(
            toolsProvider: ToolsProvider,
            playerActionProvider: PlayerActionProvider,
            nasaActionsProvider: NasaActionsProvider,
            networkStatusObservableProvider: NetworkStatusObservableProvider,
            downloadsActionProvider: DownloadsActionProvider,
            downloadsRepoProvider: DownloadsRepoProvider,
            downloadsStorageProvider: DownloadsStorageProvider,
        ): DownloadsComponent
    }

    fun inject(target: DownloadsActivity)

    fun inject(target: MediaDownloadService)

    companion object {

        fun create(
            toolsProvider: ToolsProvider,
            playerActionProvider: PlayerActionProvider,
            nasaActionsProvider: NasaActionsProvider,
            networkStatusObservableProvider: NetworkStatusObservableProvider,
            downloadsActionProvider: DownloadsActionProvider,
            downloadsRepoProvider: DownloadsRepoProvider,
            downloadsStorageProvider: DownloadsStorageProvider,
        ): DownloadsComponent {
            return DaggerDownloadsComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    playerActionProvider = playerActionProvider,
                    nasaActionsProvider = nasaActionsProvider,
                    networkStatusObservableProvider = networkStatusObservableProvider,
                    downloadsActionProvider = downloadsActionProvider,
                    downloadsRepoProvider = downloadsRepoProvider,
                    downloadsStorageProvider = downloadsStorageProvider,
                )
        }
    }
}