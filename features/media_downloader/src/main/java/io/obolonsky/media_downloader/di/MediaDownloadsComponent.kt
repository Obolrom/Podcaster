package io.obolonsky.media_downloader.di

import dagger.Component
import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.media_downloader.MediaDownloadService

@FeatureScope
@Component(
    dependencies = [
        DownloadsStorageProvider::class,
    ]
)
internal interface MediaDownloadsComponent {

    @Component.Factory
    interface Factory {

        fun create(downloadsStorageProvider: DownloadsStorageProvider): MediaDownloadsComponent
    }

    fun inject(target: MediaDownloadService)

    companion object {

        fun create(downloadsStorageProvider: DownloadsStorageProvider): MediaDownloadsComponent {
            return DaggerMediaDownloadsComponent.factory()
                .create(downloadsStorageProvider = downloadsStorageProvider)
        }
    }
}