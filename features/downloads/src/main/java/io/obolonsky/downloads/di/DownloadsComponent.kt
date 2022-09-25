package io.obolonsky.downloads.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.*
import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider
import io.obolonsky.core.di.downloads.providers.GetDownloadServiceClassActionProvider
import io.obolonsky.core.di.downloads.providers.StartDownloadServiceActionProvider
import io.obolonsky.core.di.repositories.providers.DownloadsRepoProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.downloads.ui.DownloadsActivity
import io.obolonsky.media_downloader.di.MediaDownloadsExportComponent

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
        StartDownloadServiceActionProvider::class,
        GetDownloadServiceClassActionProvider::class,
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
            startDownloadServiceActionProvider: StartDownloadServiceActionProvider,
            getDownloadServiceClassActionProvider: GetDownloadServiceClassActionProvider,
        ): DownloadsComponent
    }

    fun inject(target: DownloadsActivity)

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
            val mediaDownloadsExportComponent = MediaDownloadsExportComponent.create()

            return DaggerDownloadsComponent.factory()
                .create(
                    toolsProvider = toolsProvider,
                    playerActionProvider = playerActionProvider,
                    nasaActionsProvider = nasaActionsProvider,
                    networkStatusObservableProvider = networkStatusObservableProvider,
                    downloadsActionProvider = downloadsActionProvider,
                    downloadsRepoProvider = downloadsRepoProvider,
                    downloadsStorageProvider = downloadsStorageProvider,
                    startDownloadServiceActionProvider = mediaDownloadsExportComponent,
                    getDownloadServiceClassActionProvider = mediaDownloadsExportComponent,
                )
        }
    }
}