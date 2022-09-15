package io.obolonsky.player.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.depsproviders.PlayerActionProvider
import io.obolonsky.player.actions.ShowPlayerActionImpl
import io.obolonsky.player.actions.StopPlayerServiceAction

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
internal interface PlayerExportModule {

    @Binds
    fun bindGoToPlayerAction(action: ShowPlayerActionImpl): CreatePlayerScreenAction

    @Binds
    fun bindStopPlayerService(action: StopPlayerServiceAction): StopPlayerService
}