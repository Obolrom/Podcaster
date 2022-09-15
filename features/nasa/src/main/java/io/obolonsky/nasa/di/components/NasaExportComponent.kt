package io.obolonsky.nasa.di.components

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.GoToNasaAction
import io.obolonsky.core.di.depsproviders.NasaActionsProvider
import io.obolonsky.nasa.actions.GoToNasaActionImpl

@Component(
    modules = [
        NasaExportModule::class,
    ]
)
interface NasaExportComponent : NasaActionsProvider {

    @Component.Factory
    interface Factory {

        fun create(): NasaExportComponent
    }

    companion object {

        fun createNasaActionsProvider(): NasaActionsProvider {
            return DaggerNasaExportComponent.create()
        }
    }
}

@Module
@Suppress
internal interface NasaExportModule {

    @Binds
    fun bindGoToNasaAction(
        action: GoToNasaActionImpl
    ): GoToNasaAction
}