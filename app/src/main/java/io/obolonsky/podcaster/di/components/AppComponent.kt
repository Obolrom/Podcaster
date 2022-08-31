package io.obolonsky.podcaster.di.components

import android.content.Context
import dagger.Component
import io.obolonsky.core.di.depsproviders.ApplicationProvider
import io.obolonsky.core.di.depsproviders.PlayerActionProvider
import io.obolonsky.core.di.depsproviders.ToolsProvider
import io.obolonsky.core.di.repositories.providers.RepositoryProvider
import io.obolonsky.podcaster.PodcasterApp
import io.obolonsky.podcaster.di.modules.AppModule
import io.obolonsky.core.di.scopes.ApplicationScope
import io.obolonsky.player_feature.di.PlayerExportComponent
import io.obolonsky.podcaster.ui.MainActivity
import io.obolonsky.repository.di.RepoComponent

@ApplicationScope
@Component(
    dependencies = [
        RepositoryProvider::class,
        ToolsProvider::class,
        PlayerActionProvider::class,
    ],
    modules = [AppModule::class])
interface AppComponent : ApplicationProvider {

    @Component.Factory
    interface Factory {

        fun create(
            repositoryProvider: RepositoryProvider,
            toolsProvider: ToolsProvider,
            playerActionProvider: PlayerActionProvider,
        ): AppComponent
    }

    fun inject(target: PodcasterApp)

    fun inject(target: MainActivity)

    companion object {

        fun create(appCtx: Context): AppComponent {
            val toolsComponent = ToolsComponent.create(appCtx)
            val repoComponent = RepoComponent.create(toolsComponent)
            val playerActionsComponent = PlayerExportComponent.create()

            return DaggerAppComponent.factory()
                .create(
                    repositoryProvider = repoComponent,
                    toolsProvider = toolsComponent,
                    playerActionProvider = playerActionsComponent
                )
        }
    }
}