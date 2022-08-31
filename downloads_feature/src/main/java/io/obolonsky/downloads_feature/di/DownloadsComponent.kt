package io.obolonsky.downloads_feature.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.downloads_feature.PlayerActivity

@FeatureScope
@Component(
    dependencies = [ApplicationProvider::class],
)
interface DownloadsComponent {

    @Component.Factory
    interface Factory {

        fun create(applicationProvider: ApplicationProvider): DownloadsComponent
    }

    fun inject(target: PlayerActivity)
}