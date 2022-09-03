package io.obolonsky.downloads.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.downloads.DownloadsActivity

@FeatureScope
@Component(
    dependencies = [ApplicationProvider::class],
)
internal interface DownloadsComponent {

    @Component.Factory
    interface Factory {

        fun create(applicationProvider: ApplicationProvider): DownloadsComponent
    }

    fun inject(target: DownloadsActivity)

    companion object {

        fun create(applicationProvider: ApplicationProvider): DownloadsComponent {
            return DaggerDownloadsComponent.factory()
                .create(applicationProvider)
        }
    }
}