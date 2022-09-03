package io.obolonsky.shazam.di

import dagger.Binds
import dagger.Component
import dagger.Module
import io.obolonsky.core.di.actions.GoToShazamAction
import io.obolonsky.core.di.depsproviders.ShazamActionsProvider
import io.obolonsky.shazam.actions.GoToShazamActionImpl

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
internal interface ShazamExportModule {

    @Binds
    fun bindGoToShazamAction(action: GoToShazamActionImpl): GoToShazamAction
}