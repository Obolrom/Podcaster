package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.Component
import io.obolonsky.core.di.depsproviders.*
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.downloads.di.DownloadsExportComponent
import io.obolonsky.player.di.PlayerExportComponent
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.repository.di.RepoComponent
import io.obolonsky.shazam.di.ShazamExportComponent

@ApplicationScope
@Component(
    dependencies = [
        RepositoryProvider::class,
        ToolsProvider::class,
        PlayerActionProvider::class,
        DownloadsActionProvider::class,
        ShazamActionsProvider::class,
    ],
    modules = [AppModule::class]
)
interface AppComponent : ApplicationProvider {

    @Component.Factory
    interface Factory {

        fun create(
            repositoryProvider: RepositoryProvider,
            toolsProvider: ToolsProvider,
            playerActionProvider: PlayerActionProvider,
            downloadsActionProvider: DownloadsActionProvider,
            shazamActionsProvider: ShazamActionsProvider,
        ): AppComponent
    }

    fun inject(target: PodcasterApp)

    fun inject(target: MainActivity)

    companion object {

        fun create(appCtx: Context): AppComponent {
            val toolsProvider = ToolsComponent.create(appCtx)
            val repoProvider = RepoComponent.create(toolsProvider)
            val playerActionsProvider = PlayerExportComponent.create()
            val downloadsActionProvider = DownloadsExportComponent.create()
            val shazamActionsProvider = ShazamExportComponent.createActionsProvider()

            return DaggerAppComponent.factory()
                .create(
                    repositoryProvider = repoProvider,
                    toolsProvider = toolsProvider,
                    playerActionProvider = playerActionsProvider,
                    downloadsActionProvider = downloadsActionProvider,
                    shazamActionsProvider = shazamActionsProvider,
                )
        }
    }
}