package io.obolonsky.player_feature.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.ShowPlayer
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.depsproviders.PlayerActionProvider
import io.obolonsky.player_feature.actions.GoToPlayerAction
import io.obolonsky.player_feature.actions.StopPlayerServiceAction

@Component(
    modules = [PlayerExportModule::class]
)
interface PlayerExportComponent : PlayerActionProvider {

    @Component.Factory
    interface Factory {

        fun create(): PlayerExportComponent
    }

    companion object {

        fun create(): PlayerExportComponent {
            return DaggerPlayerExportComponent.create()
        }
    }
}

@Module
@Suppress
interface PlayerExportModule {

    @Binds
    fun bindGoToPlayerAction(action: GoToPlayerAction): ShowPlayer

    @Binds
    fun bindStopPlayerService(action: StopPlayerServiceAction): StopPlayerService
}