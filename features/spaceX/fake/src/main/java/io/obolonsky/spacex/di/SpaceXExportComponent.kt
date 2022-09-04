package io.obolonsky.spacex.di

import dagger.Component
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.GoToSpaceXAction
import io.obolonsky.core.di.depsproviders.SpaceXActionsProvider

@Component(
    modules = [
        SpaceXExportModule::class
    ]
)
interface SpaceXExportComponent : SpaceXActionsProvider {

    @Component.Factory
    interface Factory {

        fun create(): SpaceXExportComponent
    }

    companion object {

        fun createSpaceXActionsProvider(): SpaceXActionsProvider {
            return DaggerSpaceXExportComponent.create()
        }
    }
}

@Module
@Suppress
internal class SpaceXExportModule {

    @Provides
    fun bindGoToSpaceXAction(): GoToSpaceXAction {
        error("SpaceX navigation dependency is not implemented. Probably you use fake module")
    }
}