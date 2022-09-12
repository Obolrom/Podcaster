package io.obolonsky.player.di

import dagger.Component
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.CreatePlayerScreenAction
import io.obolonsky.core.di.actions.StopPlayerService
import io.obolonsky.core.di.depsproviders.PlayerActionProvider

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
class PlayerExportModule {

    @Provides
    fun bindGoToPlayerAction(): CreatePlayerScreenAction {
        error("ShowPlayer dependency is not implemented. Probably you use fake module")
    }

    @Provides
    fun bindStopPlayerService(): StopPlayerService {
        error("ShowPlayer dependency is not implemented. Probably you use fake module")
    }
}