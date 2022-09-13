package io.obolonsky.nasa.di.components

import dagger.Component
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.GoToNasaAction
import io.obolonsky.core.di.depsproviders.NasaActionsProvider

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
internal class NasaExportModule {

    @Provides
    fun provideFakeAction(): GoToNasaAction {
        error("Nasa navigation dependency is not implemented. Probably you use fake module")
    }
}