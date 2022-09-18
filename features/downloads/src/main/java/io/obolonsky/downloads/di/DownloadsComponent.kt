package io.obolonsky.downloads.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.downloads.MediaDownloadService
import io.obolonsky.downloads.ui.DownloadsActivity

@FeatureScope
@Component(
    dependencies = [ApplicationProvider::class],
    modules = [DownloadsModule::class]
)
internal interface DownloadsComponent : AssistedFactories {

    @Component.Factory
    interface Factory {

        fun create(applicationProvider: ApplicationProvider): DownloadsComponent
    }

    fun inject(target: DownloadsActivity)

    fun inject(target: MediaDownloadService)

    companion object {

        fun create(applicationProvider: ApplicationProvider): DownloadsComponent {
            return DaggerDownloadsComponent.factory()
                .create(applicationProvider)
        }
    }
}