package io.obolonsky.player.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationContextProvider
import io.obolonsky.core.di.downloads.providers.DownloadsStorageProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.player.player.PodcasterPlaybackService

@FeatureScope
@Component(
    dependencies = [
        ApplicationContextProvider::class,
        DownloadsStorageProvider::class,
    ],
    modules = [
        PlayerModule::class,
    ]
)
internal interface PlayerComponent {

    @Component.Factory
    interface Factory {

        fun create(
            appCtxProvider: ApplicationContextProvider,
            downloadsStorageProvider: DownloadsStorageProvider,
        ): PlayerComponent
    }

    fun inject(target: PodcasterPlaybackService)
}