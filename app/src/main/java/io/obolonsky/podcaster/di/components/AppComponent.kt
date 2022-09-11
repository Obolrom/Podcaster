package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.Component
import io.obolonsky.core.di.depsproviders.*
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.downloads.di.DownloadsExportComponent
import io.obolonsky.nasa.di.components.NasaExportComponent
import io.obolonsky.player.di.PlayerExportComponent
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.repository.database.di.DatabaseComponent
import io.obolonsky.repository.database.di.DatabaseComponentProvider
import io.obolonsky.repository.di.RepoComponent
import io.obolonsky.shazam.di.ShazamExportComponent
import io.obolonsky.spacex.di.SpaceXExportComponent

@ApplicationScope
@Component(
    dependencies = [
        RepositoryProvider::class,
        ToolsProvider::class,
        PlayerActionProvider::class,
        NasaActionsProvider::class,
        DatabaseComponentProvider::class,
        DownloadsActionProvider::class,
        ShazamActionsProvider::class,
        SpaceXActionsProvider::class,
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
            nasaActionsProvider: NasaActionsProvider,
            databaseComponentProvider: DatabaseComponentProvider,
            downloadsActionProvider: DownloadsActionProvider,
            shazamActionsProvider: ShazamActionsProvider,
            spaceXActionsProvider: SpaceXActionsProvider,
        ): AppComponent
    }

    fun inject(target: PodcasterApp)

    fun inject(target: MainActivity)

    companion object {

        fun create(appCtx: Context): AppComponent {
            val toolsProvider = ToolsComponent.create(appCtx)
            val playerActionsProvider = PlayerExportComponent.create()
            val downloadsActionProvider = DownloadsExportComponent.create()
            val shazamActionsProvider = ShazamExportComponent.createActionsProvider()
            val spaceXActionsProvider = SpaceXExportComponent.createSpaceXActionsProvider()
            val nasaActionsProvider = NasaExportComponent.createNasaActionsProvider()
            val databaseComponentProvider = DatabaseComponent.create(
                toolsProvider = toolsProvider,
            )
            val repoProvider = RepoComponent.create(
                toolsProvider = toolsProvider,
                databaseComponentProvider = databaseComponentProvider,
            )

            return DaggerAppComponent.factory()
                .create(
                    repositoryProvider = repoProvider,
                    toolsProvider = toolsProvider,
                    playerActionProvider = playerActionsProvider,
                    nasaActionsProvider = nasaActionsProvider,
                    databaseComponentProvider = databaseComponentProvider,
                    downloadsActionProvider = downloadsActionProvider,
                    shazamActionsProvider = shazamActionsProvider,
                    spaceXActionsProvider = spaceXActionsProvider
                )
        }
    }
}