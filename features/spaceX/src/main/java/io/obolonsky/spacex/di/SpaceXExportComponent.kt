package io.obolonsky.spacex.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.GoToSpaceXAction
import io.obolonsky.core.di.depsproviders.SpaceXActionsProvider
import io.obolonsky.spacex.actions.GoToSpaceXActionImpl

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
internal interface SpaceXExportModule {

    @Binds
    fun bindGoToSpaceXAction(action: GoToSpaceXActionImpl): GoToSpaceXAction
}