package io.obolonsky.player.di

import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.downloads.providers.GetDownloadServiceClassActionProvider
import io.obolonsky.core.di.downloads.providers.StartDownloadServiceActionProvider
import io.obolonsky.core.di.scopes.FeatureScope
import io.obolonsky.player.PlayerFragment
import io.obolonsky.player.player.PodcasterPlaybackService
import io.obolonsky.player.viewmodels.DownloadViewModel

@FeatureScope
@Component(
    dependencies = [
        ApplicationProvider::class,
        StartDownloadServiceActionProvider::class,
        GetDownloadServiceClassActionProvider::class,
    ],
    modules = [
        PlayerModule::class,
    ]
)
internal interface PlayerComponent {

    @Component.Factory
    interface Factory {

        fun create(
            applicationProvider: ApplicationProvider,
            startDownloadServiceActionProvider: StartDownloadServiceActionProvider,
            getDownloadServiceClassActionProvider: GetDownloadServiceClassActionProvider,
        ): PlayerComponent
    }

    fun inject(target: PodcasterPlaybackService)

    fun inject(target: PlayerFragment)

    fun downloadViewModelFactory(): DownloadViewModel.Factory
}