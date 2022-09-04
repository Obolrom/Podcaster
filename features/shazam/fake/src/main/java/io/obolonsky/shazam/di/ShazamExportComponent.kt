package io.obolonsky.shazam.di

import dagger.Component
import dagger.Module
import dagger.Provides
import io.obolonsky.core.di.actions.GoToShazamAction
import io.obolonsky.core.di.depsproviders.ShazamActionsProvider

@Component(
    modules = [
        ShazamExportModule::class
    ]
)
interface ShazamExportComponent : ShazamActionsProvider {

    @Component.Factory
    interface Factory {

        fun create(): ShazamExportComponent
    }

    companion object {

        fun createActionsProvider(): ShazamActionsProvider {
            return DaggerShazamExportComponent.create()
        }
    }
}

@Module
@Suppress
internal class ShazamExportModule {

    @Provides
    fun provideGoToShazamAction(): GoToShazamAction {
        error("Shazam navigation dependency is not implemented. Probably you use fake module")
    }
}